package com.esteel.common.utils;

import java.math.BigDecimal;

/**
 * @version 1.0.0
 * @ClassName CurrencyUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class CurrencyUtil {

    public static BigDecimal yuan(BigDecimal fen) {
        return BigDecimalUtil.div(fen, BigDecimalUtil.of("100"));
    }

    public static BigDecimal yuan(String fen) {
        return yuan(BigDecimalUtil.of(fen));
    }

    public static BigDecimal yuan(Integer fen) {
        return yuan(BigDecimalUtil.of(fen));
    }

    public static BigDecimal fen(BigDecimal yuan) {
        return BigDecimalUtil.mul(yuan, BigDecimalUtil.of("100"));
    }

    public static BigDecimal fen(String yuan) {
        return fen(BigDecimalUtil.of(yuan));
    }

    public static BigDecimal fen(Integer yuan) {
        return fen(BigDecimalUtil.of(yuan));
    }
}
