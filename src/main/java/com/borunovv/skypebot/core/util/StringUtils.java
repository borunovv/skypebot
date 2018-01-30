package com.borunovv.skypebot.core.util;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;

public class StringUtils {

    public static String format(String template, Locale locale, Object... params) {
        MessageFormat messageFormat = new MessageFormat(template, locale);
        return messageFormat.format(params);
    }

    public static String format(String template, Object... params) {
        MessageFormat messageFormat = new MessageFormat(template);
        return messageFormat.format(params);
    }

    public static String trim(String in) {
        return StringUtils.trim(in);
    }

    public static String join(Collection<?> parts, String delimeter) {
        return StringUtils.join(parts, delimeter);
    }

    public static String joinLR(Collection parts, String delimeter) {
        return delimeter + join(parts, delimeter) + delimeter;
    }

    public static String joinR(Collection parts, String delimeter) {
        String res = join(parts, delimeter);
        return res.isEmpty() ? res : (res + delimeter);
    }

    public static String join(Object[] parts, String delimeter) {
        return org.apache.commons.lang.StringUtils.join(parts, delimeter);
    }

    public static String join(String delimeter, Object... parts) {
        return org.apache.commons.lang.StringUtils.join(parts, delimeter);
    }

    public static java.lang.String join(Object[] array, String separator, int startIndex, int endIndex) {
        return org.apache.commons.lang.StringUtils.join(array, separator, startIndex, endIndex);
    }

    public static String[] split(String string, char delimeter) {
        return org.apache.commons.lang.StringUtils.split(string, delimeter);
    }

    public static List<String> splitToList(String string, char delimeter) {
        String[] parts = split(string, delimeter);
        return Arrays.asList(parts);
    }

    public static String joinLR(Object[] parts, String delimeter) {
        String res = join(parts, delimeter);
        return res.isEmpty() ? res : (delimeter + res + delimeter);
    }

    public static String urlEncode(String string) {
        String res = string;
        try {
            res = URLEncoder.encode(ensureString(string), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return res;
    }

    public static String urlDecode(String string) {
        String res = string;
        try {
            res = URLDecoder.decode(ensureString(string), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return res;
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || s.isEmpty();
    }

    public static String capitalize(String s) {
        return org.apache.commons.lang.StringUtils.capitalize(s);
    }

    public static String unCapitalize(String s) {
        return org.apache.commons.lang.StringUtils.uncapitalize(s);
    }

    public static boolean isNumeric(String s) {
        return org.apache.commons.lang.StringUtils.isNumeric(s);
    }

    /**
     * @param s action
     * @return hello_world => helloWorld
     *         hello-world => helloWorld
     */
    public static String modulify(String s) {

        String[] words = s.replaceAll("-", "_").split("_");
        String res = words[0];
        for (int i = 1; i < words.length; ++i) {
            res += capitalize(words[i]);
        }
        return res;
    }

    /**
     * @param s action
     * @return helloWorld => hello_world
     */
    public static String unmodulify(String s, String delimeterString) {
        char delimeter = delimeterString.charAt(0);
        StringBuilder builder = new StringBuilder(s);
        for (int i = s.length(); --i >= 0; ) {
            char ch = s.charAt(i);
            if (Character.isUpperCase(ch)) {
                builder.setCharAt(i, Character.toLowerCase(ch));
                builder.insert(i, delimeter);
            }
        }
        return builder.toString();
    }

    public static String unquote(String message) {
        message = message.substring(
                message.indexOf('"') + 1,
                message.lastIndexOf('"')
        );
        message = message.trim();
        return message;
    }

    public static String xmlEscape(String source) {
        return StringEscapeUtils.escapeXml(source);
    }

    public static String htmlEscape(String source) {
        return StringEscapeUtils.escapeHtml(source);
    }

    public static String ensureString(String source) {
        return source != null ? source : "";
    }

    public static String prefix(String base, String postfix) {
        return org.apache.commons.lang.StringUtils.removeEnd(base, postfix);
    }

    public static String suffix(String base, String postfix) {
        return org.apache.commons.lang.StringUtils.removeStart(base, postfix);
    }

    public static String[] concatenateStringArrays(String[] array, String[] array2) {
        return org.springframework.util.StringUtils.concatenateStringArrays(array, array2);
    }

    public static boolean isCapitalize(String string) {
        return capitalize(string).equals(string);
    }


    public static String substringBefore(String str, String separator) {
        return org.apache.commons.lang.StringUtils.substringBefore(str, separator);
    }

    public static String substringAfter(String str, String separator) {
        return org.apache.commons.lang.StringUtils.substringAfter(str, separator);
    }

    public static String substringAfterLast(String str, String separator) {
        return org.apache.commons.lang.StringUtils.substringAfterLast(str, separator);
    }

    public static Map<String, String> parseParams(String paramString) {
        Map<String, String> res = new HashMap<String, String>();
        String[] rowParams = paramString.split("&");
        for (String rowParam : rowParams) {
            String[] params = rowParam.split("=");
            if (params.length == 2) {
                res.put(urlDecode(params[0]), urlDecode(params[1]));
            }
        }
        return res;
    }

    public static Map<String, List<String>> parseParamsWithDuplicates(String paramString) {
        Map<String, List<String>> res = new HashMap<String, List<String>>();
        String[] rowParams = paramString.split("&");
        for (String rowParam : rowParams) {
            String[] params = rowParam.split("=");
            if (params.length == 2) {
                String name = StringUtils.urlDecode(params[0]);
                String value = StringUtils.urlDecode(params[1]);
                if (res.containsKey(name)) {
                    res.get(name).add(value);
                } else {
                    res.put(name, Arrays.asList(value));
                }
            }
        }
        return res;
    }

    public static String reverse(String string) {
        return org.apache.commons.lang.StringUtils.reverse(string);
    }

    public static String chomp(String string, String separator) {
        return org.apache.commons.lang.StringUtils.chomp(string, separator);
    }

    public static String getCommonPrefix(String... strs) {
        return org.apache.commons.lang.StringUtils.getCommonPrefix(strs);
    }

    public static String removeEnd(String string, String remove) {
        return org.apache.commons.lang.StringUtils.removeEnd(string, remove);
    }

    public static String removeStart(String string, String remove) {
        return org.apache.commons.lang.StringUtils.removeStart(string, remove);
    }

    /**
     * @param left  левая часть
     * @param right правая часть
     * @return возвращает общую часть с конца левой части и с начала правой
     *         <p/>
     *         example: commonPartEndLeftAndBeginRight("123", "235") -> "23"
     */
    public static String commonPartEndLeftAndBeginRight(String left, String right) {
        String res = "";
        int leftLength = left.length();
        int length = Math.min(leftLength, right.length());
        for (int i = leftLength - length; i < leftLength; ++i) {
            String substring = left.substring(i);
            if (right.startsWith(substring)) {
                res = substring;
                break;
            }
        }
        return res;
    }

    public static String newStringUtf8(byte[] bytes) {
        return org.apache.commons.codec.binary.StringUtils.newString(
                bytes, CharEncoding.UTF_8);
    }

    public static byte[] getBytesUtf8(String str) {
        return org.apache.commons.codec.binary.StringUtils.getBytesUtf8(str);
    }
}
