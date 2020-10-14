package com.esteel.common.utils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0.0
 * @ClassName StringUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class StringUtil {

    private static final int[] DIGIT_UNICODE = {'\u0030', '\u0039'};
    private static final int[] UPPER_CASE_ALPHABETIC_UNICODE = {'\u0041', '\u005A'};
    private static final int[] LOWER_CASE_ALPHABETIC_UNICODE = {'\u0061', '\u007A'};

    private static final int[][] SUPPORTED_UNICODE = {
            //C0控制符及基本拉丁文
            {'\u0000', '\u007F'},
            //常用标点
            {'\u2000', '\u206F'},
            //CJK 符号和标点
            {'\u3000', '\u303F'},
            //半型及全型形式
            {'\uFF00', '\uFFEF'},
            //CJK 统一表意符号, 中文字符
            {'\u4E00', '\u9FBF'}
    };


    private StringUtil() {}

    public static String format(String format, Object... values) {
        if (StringUtils.isBlank(format) || values == null || values.length == 0) {
            return null;
        }
        for (Object v : values) {
            if (v == null) {
                return null;
            }
        }
        return String.format(format, values);
    }

    public static String toSnakeCase(String s) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s);
    }

    public static String toCamelCase(String s) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, s);
    }

    /**
     * 字符串加星处理
     *
     * @param plainText 原文字符串
     * @param startCursor 头部不加星长度
     * @param endCursor 尾部不加星长度
     * @return 加星字符串
     */
    public static String addStarText(String plainText, int startCursor, int endCursor) {

        if (StringUtils.isBlank(plainText)) {
            return plainText;
        }

        if (startCursor < 0 || startCursor >= plainText.length() ||
                endCursor < 0 || endCursor >= plainText.length()) {
            return plainText;
        }

        StringBuilder addStarText = new StringBuilder();
        addStarText.append(plainText.substring(0, startCursor)); // 头部不加星
        int starLength = plainText.length() - (startCursor + endCursor);
        char[] stars = new char[starLength];
        Arrays.fill(stars, '*');
        addStarText.append(stars); // 中间加星
        addStarText.append(plainText.substring(plainText.length() - endCursor)); // 尾部不加星

        return addStarText.toString();
    }

    public static String trimAll(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        return text.replaceAll("\\s+|\t|\r|\n|↵", " ");
    }

    public static boolean isAlphabeticOrDigit(String text) {
        for (int i = 0; i < text.length(); i ++) {
            int codePoint = text.codePointAt(i);
            boolean digit = codePoint >= DIGIT_UNICODE[0] && codePoint <= DIGIT_UNICODE[1];
            boolean alphabetic = (codePoint >= UPPER_CASE_ALPHABETIC_UNICODE[0] && codePoint <= UPPER_CASE_ALPHABETIC_UNICODE[1]) ||
                    (codePoint >= LOWER_CASE_ALPHABETIC_UNICODE[0] && codePoint <= LOWER_CASE_ALPHABETIC_UNICODE[1]);
            if (!digit && !alphabetic) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getUnsupportedChars(String text) {
        List<String> unsupported = Lists.newArrayList();
        for (int i = 0; i < text.length();) {
            int codePoint = text.codePointAt(i);
            boolean supplementary = Character.isSupplementaryCodePoint(codePoint);
            i += supplementary ? 2 : 1;
            boolean support = false;
            for (int[] range : SUPPORTED_UNICODE) {
                support = codePoint >= range[0] && codePoint <= range[1];
                if (support) {
                    break;
                }
            }
            if (!support) {
                unsupported.add(new String(Character.toChars(codePoint)));
            }
        }
        return unsupported;
    }

    public static void main(String[] args) {
        String s = "！，。《》、：；「」｛｝～`……％&＊#@￥（）——+=-";
        List<String> unsupported = getUnsupportedChars(s);
        System.out.println(unsupported);

//        String text = "中午abc";
        String text = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890中午";
        boolean alphabeticOrDigit = isAlphabeticOrDigit(text);
        System.out.println(alphabeticOrDigit);
    }
}
