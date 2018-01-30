package com.borunovv.skypebot.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author borunovv
 */
public final class TimeUtils {

    private static final String YYYY_MM_DD__HH_MM_SS_Z = "yyyy-MM-dd HH:mm:ss (z)";
    private static final String YYYY_MM_DD__HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat YYYY_MM_DD__HH_MM_SS_GMT = getGMTFormatter();

    public static String toString(Date date) {
        return date == null ?
                "-" :
                new SimpleDateFormat(YYYY_MM_DD__HH_MM_SS_Z).format(date);
    }

    public static String toStringUTC(Date date) {
        return date == null ?
                "-" :
                YYYY_MM_DD__HH_MM_SS_GMT.format(date);
    }

    public static Date parseDateUTC(String str) {
        try {
            return YYYY_MM_DD__HH_MM_SS_GMT.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date GMT: '" + str + "'", e);
        }
    }

    private static SimpleDateFormat getGMTFormatter() {
        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD__HH_MM_SS);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format;
    }

    // seconds -> "12:34:56"
    public static String secondsToTime(long secondsTotal) {
        int hours = (int) (secondsTotal / 3600) % 24;
        int minutes = (int) (secondsTotal / 60) % 60;
        int seconds = (int) secondsTotal % 60;

        return (hours < 10 ? "0" : "") + hours + ":" +
                (minutes < 10 ? "0" : "") + minutes + ":" +
                (seconds < 10 ? "0" : "") + seconds;
    }
}
