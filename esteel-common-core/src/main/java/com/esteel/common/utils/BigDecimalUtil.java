package com.esteel.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static java.math.BigDecimal.ZERO;

/**
 * @version 1.0.0
 * @ClassName BigDecimalUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class BigDecimalUtil {

    // 默认除法运算精度
    private static final int DEFAULT_DIV_SCALE = 10;

    // 默认金额格式
    private static final DecimalFormat DEFAULT_MONEY_FORMAT = new DecimalFormat("###,##0.00");

    private BigDecimalUtil() {}

    public static BigDecimal of(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return new BigDecimal(value);
    }

    public static BigDecimal of(Integer value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    public static BigDecimal of(Double value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value);
    }

    public static BigDecimal scale(Double n, int newScale, int roundingMode) {
        return scale(of(n), newScale, roundingMode);
    }

    public static BigDecimal scale(String n, int newScale, int roundingMode) {
        return scale(of(n), newScale, roundingMode);
    }

    public static BigDecimal scale(BigDecimal n, int newScale, int roundingMode) {
        if (n == null) {
            return null;
        }
        return n.setScale(newScale, roundingMode);
    }

    /**
     * 多个参数相加
     *
     * @param vs
     * @author min.zhang
     */
    public static BigDecimal add(BigDecimal... vs) {
        BigDecimal sum = new BigDecimal("0");
        for (BigDecimal value : vs) {
            BigDecimal v = value == null ? ZERO : value;
            sum = sum.add(v);
        }
        return sum;
    }


    /**
     * 多个数相减
     *
     * @param vs
     */
    public static BigDecimal sub(BigDecimal... vs) {
        BigDecimal sub = vs.length == 0 ? new BigDecimal("0") : vs[0];
        if (sub == null) {
            throw new IllegalArgumentException("Null value in index 0");
        }
        for (int i = 1; i < vs.length; i++) {
            BigDecimal value = vs[i];
            if (value == null) {
                throw new IllegalArgumentException("Null value in index " + i);
            }
            sub = sub.subtract(value);
        }
        return sub;
    }


    /**
     * 多个数相乘
     *
     * @param vs
     */
    public static BigDecimal mul(BigDecimal... vs) {
        BigDecimal sub = vs.length == 0 ? new BigDecimal("0") : vs[0];
        for (int i = 1; i < vs.length; i++) {
            BigDecimal value = vs[i];
            sub = sub.multiply(value);
        }
        return sub;
    }

    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return div(v1, v2, DEFAULT_DIV_SCALE);
    }


    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后 10 位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数 两个参数的商
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (v1 == null || v2 == null) {
            return null;
        }
        if (isZero(v2)) {
            return ZERO;
        }
        return v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 判断数字是否为0。
     *
     * @param d
     */
    public static boolean isZero(BigDecimal d) {
        return d != null && d.compareTo(ZERO) == 0;
    }

    /**
     * 格式化BigDecimal
     *
     * @param v
     * @return
     * @see #DEFAULT_MONEY_FORMAT
     */
    public static String format(BigDecimal v) {
        return DEFAULT_MONEY_FORMAT.format(v);
    }
}
