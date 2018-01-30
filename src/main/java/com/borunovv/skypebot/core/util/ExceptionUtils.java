package com.borunovv.skypebot.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ExceptionUtils {
    private static final String NEW_LINE = "\n";

    public static String exceptionFullInfo(Exception e) {
        return exceptionFullInfo(e, e.getStackTrace().length);
    }

    public static String exceptionFullInfo(Exception e, int stackTraceDeep) {
        StringBuilder sb = new StringBuilder();
        sb.append("exception: ").append(e.getClass().getName()).append(NEW_LINE);
        sb.append("message: ").append(e.getMessage()).append(NEW_LINE);
        sb.append(StringUtils.join(e.getStackTrace(), NEW_LINE, 0, Math.min(stackTraceDeep, e.getStackTrace().length)));
        return sb.toString();
    }
    
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
