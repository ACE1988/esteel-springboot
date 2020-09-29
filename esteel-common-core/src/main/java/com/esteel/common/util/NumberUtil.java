package com.esteel.common.util;

/**
 * @version 1.0.0
 * @ClassName NumberUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class NumberUtil {

    public static final String[] DIGITAL_CHINESE = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    public static boolean isNumerical(String string) {
        if (string == null || string.isEmpty()) {
            return false;
        }
        int i = 0;
        if (string.charAt(0) == '-') {
            if (string.length() > 1) {
                i ++;
            } else {
                return false;
            }
        }
        for (; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
