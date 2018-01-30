package com.borunovv.skypebot.core.util;


import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class IPUtils {

    public static long[] getNumberIps(String[] ips) {
        long[] result = null;
        if (ips != null) {
            result = new long[ips.length];
            for (int i = 0; i < ips.length; i++) {
                result[i] = getIP(ips[i]);
            }
        }
        return result;
    }

    public static List<Long> filterLanIps(String[] ips) {//Отсекает все LAN
        List<Long> res = null;
        long[] numberIps = getNumberIps(ips);
        if (numberIps != null) {
            res = new LinkedList<Long>();
            for (Long ip : numberIps) {
                if (!isLanIp(ip)) res.add(ip);
            }
        }
        return res;
    }

    public static boolean isLanIp(String ip) {
        return isLanIp(getIP(ip));
    }

    public static boolean isLanIp(long ip) {
        return ip >= 2130706433 && ip <= 2130706433
                || ip >= 167772160 && ip <= 184549375
                || ip >= 2886729728l && ip <= 2887778303l
                || ip >= 3232235520l && ip <= 3232301055l;
    }


    public static long getIP(InetAddress address) {
        return new java.math.BigInteger(1, address.getAddress()).longValue();
    }

    public static long getIP(String address) {
        try {
            return new BigInteger(1, InetAddress.getByName(address.trim()).getAddress()).longValue();
        } catch (UnknownHostException e) {
            return 0;
        }
    }

    public static String getIP(long ip) {
        try {
            byte[] bytes = BigInteger.valueOf(ip).toByteArray();
            byte[] finalBytes = new byte[4];

            if (bytes.length > 4) {
                for (int i = 1; i < 5; i++)
                    finalBytes[i - 1] = bytes[i];
            } else finalBytes = bytes;

            return InetAddress.getByAddress(finalBytes).getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String getLocalHostIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    public static long ipToLong(String ipAsStr) throws IllegalArgumentException {
        if (StringUtils.isNullOrEmpty(ipAsStr)) {
            throw new IllegalArgumentException();
        }
        String[] parts = ipAsStr.split("\\.");
        if (parts.length < 4 || parts.length > 6) {
            throw new IllegalArgumentException("Bad ip: " + ipAsStr);
        }
        long ip = 0;
        for (int i = 0; i < parts.length; ++i) {
            long part = Long.parseLong(parts[i]);
            if (part < 0 || part > 255) {
                throw new IllegalArgumentException("Bad ip: " + ipAsStr);
            }
            ip = ip * 256 + part;
        }
        return ip;
    }

    public static String ipToString(long ip) {
        if (ip > 0xFFFFFFFFFFFFL || ip < 0) {
            throw new IllegalArgumentException("Wrong ip: " + ip);
        }
        String result;
        String v4Ip = ""
                + ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
        result = v4Ip;
        if (ip > 0xFFFFFFFFL) {
            result = ""
                    + ((ip >> 40) & 0xFF) + "."
                    + ((ip >> 32) & 0xFF) + "."
                    + v4Ip;
        }
        return result;
    }
}
