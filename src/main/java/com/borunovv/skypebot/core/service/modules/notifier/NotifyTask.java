package com.borunovv.skypebot.core.service.modules.notifier;

import com.borunovv.skypebot.core.service.IMarshallable;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author borunovv
 */
class NotifyTask implements IMarshallable {

    private static final AtomicLong lastId = new AtomicLong(0);

    private long id = lastId.incrementAndGet();
    private String userOrConversationId;
    private String userName;
    private String message;
    private SceduleUTC sceduleUTC;

    private long lastDelta = Long.MAX_VALUE;

    public NotifyTask() {
    }

    public NotifyTask(String userOrConversationId,
                      String userName,
                      String message,
                      SceduleUTC sceduleUTC) {
        this.userOrConversationId = userOrConversationId;
        this.message = message;
        this.userName = userName;
        this.sceduleUTC = sceduleUTC;
    }

    public long getId() {
        return id;
    }

    public String getUserOrConversationId() {
        return userOrConversationId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public SceduleUTC getSceduleUTC() {
        return sceduleUTC;
    }

    public boolean update(DateTime nowTime) {
        long newDelta = sceduleUTC.getDeltaSecondsBeforeNotify(nowTime);

        boolean needNotify = false;
        if (sceduleUTC.isSingle()) {
            needNotify = (lastDelta > 0 && newDelta <= 0);

        } else if (sceduleUTC.isPeriodic()){
            needNotify = newDelta > lastDelta;
        }

        lastDelta = newDelta;

        return needNotify;
    }

    public boolean isExpired(DateTime nowTime) {
        return sceduleUTC.isExpired(nowTime);
    }

    @Override
    public String toString() {
        return "NotifyTask{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", sceduleUTC=" + sceduleUTC +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public Map<String, Object> marshall() {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("id", id);
        res.put("userOrConversationId", userOrConversationId);
        res.put("userName", userName);
        res.put("message", message);
        res.put("sceduleUTC", sceduleUTC.marshall());
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void unmarshall(Map<String, Object> params) {
        id = Long.parseLong(params.get("id").toString());
        userOrConversationId = params.get("userOrConversationId").toString();
        userName = params.get("userName").toString();
        message = params.get("message").toString();
        sceduleUTC = new SceduleUTC();
        sceduleUTC.unmarshall((Map<String, Object>) params.get("sceduleUTC"));
    }
}