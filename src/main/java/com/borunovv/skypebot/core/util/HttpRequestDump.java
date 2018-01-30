package com.borunovv.skypebot.core.util;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author borunovv
 */
public class HttpRequestDump {

    public static String requestToString(HttpServletRequest request) {
        return requestToString(request, true);
    }

    public static String requestToString(HttpServletRequest request, boolean showPostData) {
        Assert.notNull(request, "request is null");

        StringBuilder sb = new StringBuilder();
        sb.append("method: ").append(request.getMethod()).append("\n");
        sb.append("url: ").append(getUrl(request)).append("\n");
        sb.append("ip: ").append(getStringIP(request)).append("\n");
        sb.append("user-agent: ").append(getUserAgent(request)).append("\n");
        sb.append("params map:\n");
        Enumeration params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = (String) params.nextElement();
            String[] values = request.getParameterValues(name);
            if (values.length == 1) {
                sb.append("  ").append(name).append("=").append(values[0]).append("\n");
            } else {
                sb.append("  ").append(name).append("=").append(Arrays.toString(values)).append("\n");
            }
        }

        // Get headers.
        Enumeration names = request.getHeaderNames();
        sb.append("\nRequest headers: \n");
        while (names.hasMoreElements()) {
            String hdrName = (String) names.nextElement();
            Enumeration headers = request.getHeaders(hdrName);
            while (headers.hasMoreElements()) {
                String value = (String) headers.nextElement();
                sb.append("  ").append(hdrName).append(": ").append(value).append("\n");
            }
        }

        sb.append("\nRemote address of the client or last proxy: ")
                .append(request.getRemoteAddr()).append("\n");

        if (showPostData) {
            try {
                byte[] data = IOUtils.inputStreamToByteArray(request.getInputStream());
                sb.append("\nPost data as string: '").append(StringUtils.newStringUtf8(data)).append("'\n");

                sb.append("\nPost data as byte array:\n");
                sb.append(ArrayUtils.toString(data));
                sb.append("\n");
            } catch (IOException e) {
                sb.append("\nPost data is empty (or failed to get)");
            }
        }

        return sb.toString();
    }


    @SuppressWarnings({"unchecked"})
    public static Map<String, String> requestParamMap(HttpServletRequest request) {
        return requestParamMap(request.getParameterMap());
    }

    public static Map<String, String> requestParamMap(Map<String, String[]> params) {
        Map<String, String> res = new HashMap<String, String>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (String value : values) {
                res.put(key, value);
            }
        }
        return res;
    }

    public static String getUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        return uri(request, false) + (query != null ? ("?" + query) : "");
    }

    private static String uri(HttpServletRequest request, boolean isEndDelimeter) {
        String uri = StringUtils.suffix(request.getRequestURI(), request.getContextPath());
        return isEndDelimeter ? (uri.endsWith("/") ? uri : (uri + "/")) : uri;
    }

    public static String getUserAgent(HttpServletRequest request) {
        String operaMiniAgent = request.getHeader("X-OperaMini-Phone-UA");
        String res = operaMiniAgent != null ? operaMiniAgent : request.getHeader("user-agent");
        return res != null ? res : "";
    }

    public static String[] getXForwardedFor(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null) {
            return xForwardedFor.split(",");
        } else {
            return null;
        }
    }

    public static long getIP(HttpServletRequest request) {
        List<Long> ips = getIPs(request);
        if (ips != null && ips.size() > 0) return ips.get(0);
        return IPUtils.getIP(request.getRemoteAddr());
    }

    public static String getStringIP(HttpServletRequest request) {
        return IPUtils.getIP(getIP(request));
    }

    public static List<Long> getIPs(HttpServletRequest request) {
        List<String> all = new ArrayList<String>();
        String[] ips = getXForwardedFor(request);
        if (ips != null)  {
            Collections.addAll(all, ips);
        }
        String ip = request.getRemoteAddr();
        if (!StringUtils.isNullOrEmpty(ip) && !all.contains(ip)) {
            all.add(ip);
        }
        String[] result = new String[all.size()];
        all.toArray(result);
        return IPUtils.filterLanIps(result);
    }
}