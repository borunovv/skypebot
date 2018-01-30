package com.borunovv.skypebot.core.service.modules.notifier;

import com.borunovv.skypebot.core.service.IMarshallable;
import com.borunovv.skypebot.core.util.TimeUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author borunovv
 */
final class SceduleUTC implements IMarshallable {
    private int hourUTC;
    private int minuteUTC;
    private Set<Integer> repeatOnWeekDays;
    private DateTime singleDateTime;

    public SceduleUTC() {
    }

    static SceduleUTC makeSingle(DateTime date) {
        SceduleUTC res = new SceduleUTC();
        res.singleDateTime = date.withSecondOfMinute(0);
        return res;
    }

    static SceduleUTC makePeriodic(int hourUTC, int minuteUTC, int[] daysOfWeek) {
        Assert.isTrue(hourUTC >= 0 && hourUTC <= 23, "Bad hour: " + hourUTC);
        Assert.isTrue(minuteUTC >= 0 && minuteUTC <= 59, "Bad minute: " + minuteUTC);
        Assert.notNull(daysOfWeek, "daysOfWeek is null");
        Assert.isTrue(daysOfWeek.length > 0, "daysOfWeek is empty");

        SceduleUTC res = new SceduleUTC();
        res.hourUTC = hourUTC;
        res.minuteUTC = minuteUTC;
        res.repeatOnWeekDays = new HashSet<Integer>();
        for (int day : daysOfWeek) {
            Assert.isTrue(day >= 1 && day <= 7, "Dab day index: " + day);
            res.repeatOnWeekDays.add(day);
        }
        return res;
    }

    public boolean isSingle() {
        return singleDateTime != null;
    }

    public boolean isPeriodic() {
        return repeatOnWeekDays != null && repeatOnWeekDays.size() > 0;
    }

    public boolean isExpired(DateTime nowTime) {
        return isSingle() && singleDateTime.isBefore(nowTime);
    }

    public boolean isSet() {
        return isSingle() || isPeriodic();
    }

    public long getDeltaSecondsBeforeNotify(DateTime nowTime) {
        if (!isSet()) return -1;
        if (isSingle()) {
            Seconds seconds = Seconds.secondsBetween(nowTime, singleDateTime);
            long delta = seconds.getSeconds();
            return delta;
        } else if (isPeriodic()) {
            return getPeriodicDelta(nowTime);
        } else {
            throw new RuntimeException("Undefined state for " + this + "\nnowTime: " + nowTime);
        }
    }

    private long getPeriodicDelta(DateTime nowTime) {
        DateTime dateTimeUTC = new DateTime(nowTime);
        int nowDayOfWeekUTC = dateTimeUTC.getDayOfWeek(); // 1=MON..7=SUN
        int nowHourUTC = dateTimeUTC.getHourOfDay();
        int nowMinuteUTC = dateTimeUTC.getMinuteOfHour();
        int nowSecondsUTS = dateTimeUTC.getSecondOfMinute();

        long minDelta = Long.MAX_VALUE;
        for (Integer day : repeatOnWeekDays) {
            int deltaDays = day - nowDayOfWeekUTC;
            if (deltaDays < 0) {
                deltaDays += 7;
            }
            long nowOffsetInSeconds = (nowHourUTC * 60 + nowMinuteUTC) * 60 + nowSecondsUTS;
            long notifyOffsetInSeconds = (hourUTC * 60 + minuteUTC) * 60;

            long deltaSeconds = notifyOffsetInSeconds - nowOffsetInSeconds + deltaDays * 24 * 60 * 60;
            if (deltaSeconds >= 0) {
                minDelta = Math.min(minDelta, deltaSeconds);
            }
        }

        return minDelta;
    }

    @Override
    public String toString() {
        if (isSingle()) {
            return "Single: " + singleDateTime.toString("yyyy-MM-dd HH:mm Z");
        }
        if (isPeriodic()) {
            List<Integer> orderedDays = new ArrayList(repeatOnWeekDays);
            Collections.sort(orderedDays);
            String days = "(";
            boolean isFirst = true;
            for (Integer orderedDay : orderedDays) {
                if (!isFirst) {
                    days += ",";
                }
                isFirst = false;
                days += orderedDay;
            }
            days += ")";

            return "Periodic: " + (hourUTC < 10 ? "0" + hourUTC : hourUTC) + ":"
                    + (minuteUTC < 10 ? "0" + minuteUTC : minuteUTC)
                    + ", days: " + days;
        }
        return "[Empty]";
    }

    @Override
    public Map<String, Object> marshall() {
        Map<String, Object> res = new HashMap<String, Object>();

        res.put("hourUTC", hourUTC);
        res.put("minuteUTC", minuteUTC);
        if (isPeriodic()) {
            res.put("repeatOnWeekDays", new ArrayList<Integer>(repeatOnWeekDays));
        } else if (isSingle()) {
            res.put("singleDateTime", TimeUtils.toStringUTC(singleDateTime.toDate()));
        }

        return res;
    }

    @Override
    public void unmarshall(Map<String, Object> params) {
        Assert.notNull(params, "null params come");
        hourUTC = Integer.parseInt(params.get("hourUTC").toString());
        minuteUTC = Integer.parseInt(params.get("minuteUTC").toString());
        if (params.containsKey("repeatOnWeekDays")) {
            List<Number> days = (List<Number>) params.get("repeatOnWeekDays");
            repeatOnWeekDays = new HashSet<Integer>();
            for (Number day : days) {
                repeatOnWeekDays.add(day.intValue());
            }
        }
        if (params.containsKey("singleDateTime")) {
            singleDateTime = new DateTime(TimeUtils.parseDateUTC(params.get("singleDateTime").toString()));
        }
    }
}