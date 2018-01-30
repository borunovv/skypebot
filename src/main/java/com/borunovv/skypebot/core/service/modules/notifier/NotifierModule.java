package com.borunovv.skypebot.core.service.modules.notifier;

import com.borunovv.skypebot.core.service.modules.AbstractModule;
import com.borunovv.skypebot.core.service.modules.ModuleException;
import com.borunovv.skypebot.core.service.modules.Request;
import com.borunovv.skypebot.core.service.skype.SkypeChatService;
import com.borunovv.skypebot.core.util.TimeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author borunovv
 */
@Service
public class NotifierModule extends AbstractModule implements DisposableBean {

    private List<NotifyTask> tasks = new CopyOnWriteArrayList<NotifyTask>();
    private volatile boolean stopRequested = false;

    @Override
    protected void onInit() {
        super.onInit();
        startWorkerThread();
    }

    @Override
    public void onDestroy(){
        stopWorkerThread();
        super.onDestroy();
    }

    protected Map<String, Object> getConfig() {
        Map<String, Object> res = new HashMap<String, Object>();
        List<Map<String, Object>> taskList = new ArrayList<Map<String, Object>>(tasks.size());
        for (NotifyTask task : tasks) {
            taskList.add(task.marshall());
        }
        res.put("tasks", taskList);
        return res;
    }


    @SuppressWarnings("unchecked")
    protected void setConfig(Map<String, Object> params) {
        tasks.clear();
        List<Map<String, Object>> taskList = (List<Map<String, Object>>) params.get("tasks");
        for (Map<String, Object> marshalledTask : taskList) {
            NotifyTask newTask = new NotifyTask();
            newTask.unmarshall(marshalledTask);
            tasks.add(newTask);
        }
    }

    private void startWorkerThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stopRequested) {
                    try {
                        doIteration();
                    } catch (Exception e) {
                        LOG.error("Error in worker thread", e);
                    }
                }
            }
        }).start();
    }

    private void stopWorkerThread() {
        stopRequested = true;
    }

    // Calls from Separate thread!
    private void doIteration() {
        DateTime nowTimeUTC = DateTime.now(DateTimeZone.UTC);
        List<NotifyTask> listCopy = new ArrayList<NotifyTask>(tasks);
        for (NotifyTask task : listCopy) {
            boolean needNotify = task.update(nowTimeUTC);
            if (needNotify) {
                doNotify(task);
            }
            if (task.isExpired(nowTimeUTC)) {
                tasks.remove(task);
                LOG.info("Task expired (removed): " + task);
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    private void doNotify(NotifyTask task) {
        skype.sendTextMessage(
                task.getUserOrConversationId(),
                task.getUserName(),
                "<b>Notification: " + task.getMessage() + "</b>");
    }


    @Override
    public void process(Request request) throws ModuleException {
        // <cmd> [params]
        String commandLine = request.text.trim();
        if (commandLine.isEmpty()) {
            throw new ModuleException("Expected command: <cmd> [prams]");
        }
        String result = processCommand(commandLine, request);
        skype.sendTextMessage(request.userId, request.userName, result);
    }

    private String processCommand(String command, Request request) {
        Pattern CMD_PATTERN = Pattern.compile("(\\w+)\\s*(.*)");
        Matcher matcher = CMD_PATTERN.matcher(command);
        if (!matcher.matches()) {
            throw new ModuleException("Expected command: <cmd> [params]");
        }

        String cmd = matcher.group(1);
        String args = matcher.group(2).trim();

        if (cmd.equalsIgnoreCase("help")) {
            return processHelp();
        } else if (cmd.equalsIgnoreCase("add")) {
            return processAdd(args, request);
        } else if (cmd.equalsIgnoreCase("list")) {
            return processList(request);
        } else if (cmd.equalsIgnoreCase("delete")) {
            return processDelete(request, args);
        } else if (cmd.equalsIgnoreCase("clear")) {
            return processClear(request);
        }

        throw new ModuleException("Undefined command '" + cmd + "'");
    }

    private String processClear(Request request) {
        List<NotifyTask> listCopy = new ArrayList<NotifyTask>(tasks);
        for (NotifyTask task : listCopy) {
            if (task.getUserOrConversationId().equals(request.userId)) {
                tasks.remove(task);
            }
        }
        return "OK";
    }

    private String processHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Possible commands:\r\n");
        sb.append("<b>add</b> HH:MM \"Any message\" [1,2,3,4,5,6,7]").append("\r\n");
        sb.append("<b>list</b>").append("\r\n");
        sb.append("<b>delete</b> [id]").append("\r\n");
        sb.append("<b>clear</b>").append("\r\n");
        return sb.toString();
    }

    private String processAdd(String args, Request request) {
        // HH:MM "Text" 1,3,4
        Pattern PARAMS_PATTERN = Pattern.compile("(\\d+):(\\d+)\\s+\"([^\"]*)\"\\s*([^\\s]*)");
        Matcher matcher = PARAMS_PATTERN.matcher(args);

        if (!matcher.matches()) {
            throw new ModuleException("Bad arguments. Format: HH:MM \"Text\" [1,2,3,4,5,6,7]");
        }

        int hours = Integer.parseInt(matcher.group(1));
        int minutes = Integer.parseInt(matcher.group(2));
        String message = matcher.group(3);
        if (message.isEmpty()) {
            throw new ModuleException("The message is empty");
        }

        String[] daysStr = matcher.group(4).isEmpty() ?
                new String[0] :
                matcher.group(4).split(",");

        int[] days = new int[daysStr.length];

        int i = 0;
        for (String dayStr : daysStr) {
            int day = Integer.parseInt(dayStr);
            if (day <= 0 || day > 7) throw new ModuleException("Bad day number: " + day);
            days[i++] = day;
        }

        boolean isPeriodic = days.length > 0;

        SceduleUTC sceduleUTC;
        if (isPeriodic) {
            sceduleUTC = SceduleUTC.makePeriodic(hours, minutes, days);
        } else {
            DateTime time = new DateTime(DateTimeZone.UTC)
                    .withHourOfDay(hours)
                    .withMinuteOfHour(minutes);

            sceduleUTC = SceduleUTC.makeSingle(time);
            if (sceduleUTC.isExpired(new DateTime(DateTimeZone.UTC))) {
                throw new ModuleException("Time already expired");
            }
        }

        tasks.add(new NotifyTask(request.userId, request.userName, message, sceduleUTC));
        saveConfig();

        return "OK";
    }

    private String processList(Request request) {
        StringBuilder sb = new StringBuilder();

        for (NotifyTask task : tasks) {
            if (task.getUserOrConversationId().equals(request.userId)) {
                sb.append(task.getId()).append(": ")
                        .append(task.getSceduleUTC())
                        .append(" \"").append(task.getMessage()).append("\"")
                        .append(" [next in ").append(
                        TimeUtils.secondsToTime(task.getSceduleUTC().getDeltaSecondsBeforeNotify(DateTime.now(DateTimeZone.UTC))))
                        .append("]")
                        .append("\r\n");
            }
        }

        if (sb.length() == 0) {
            return "[empty]";
        }

        return sb.toString();
    }

    private String processDelete(Request request, String args) {
        long id;
        try {
            id = Long.parseLong(args);
        } catch (Exception e) {
            throw new ModuleException("Bad argument '" + args + "', expected long id");
        }

        for (NotifyTask task : tasks) {
            if (task.getUserOrConversationId().equals(request.userId)) {
                if (task.getId() == id) {
                    tasks.remove(task);
                    saveConfig();
                    return "OK";
                }
            }
        }

        return "Not found task #" + id;
    }

    @Inject
    private SkypeChatService skype;
}
