package com.esteel.common.utils;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version 1.0.0
 * @ClassName DateConvert.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-10月-13日  10:41
 */
public class DateConvert implements Converter {

    public static final Logger LOGGER = LoggerFactory.getLogger(DateConvert.class);
    private static String dateFormatStr = "yyyy-MM-dd";
    private static SimpleDateFormat dateTimeFormat;
    private static String dateLongFormatStr;
    private static SimpleDateFormat dateTimeLongFormat;

    public DateConvert() {
    }

    @Override
    public Object convert(Class arg0, Object arg1) {
        if (arg1 == null) {
            return null;
        } else {
            LOGGER.info(arg1.getClass().getName() + "=" + arg1.toString());
            String className = arg1.getClass().getName();
            SimpleDateFormat df;
            if (!"java.sql.Timestamp".equalsIgnoreCase(className) && !"java.util.Date".equalsIgnoreCase(className) && !"java.sql.Date".equalsIgnoreCase(className)) {
                String p = (String)arg1;
                if (p != null && p.trim().length() != 0) {
                    try {
                        df = new SimpleDateFormat(dateFormatStr + " HH:mm:ss");
                        return df.parse(p.trim());
                    } catch (Exception var9) {
                        try {
                            df = new SimpleDateFormat(dateFormatStr);
                            return df.parse(p.trim());
                        } catch (ParseException var8) {
                            var9.printStackTrace();
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            } else {
                try {
                    df = new SimpleDateFormat(dateFormatStr + " HH:mm:ss");
                    return df.parse(dateTimeLongFormat.format(arg1));
                } catch (Exception var10) {
                    try {
                        df = new SimpleDateFormat(dateFormatStr);
                        return df.parse(dateTimeFormat.format(arg1));
                    } catch (ParseException var7) {
                        var10.printStackTrace();
                        return null;
                    }
                }
            }
        }
    }

    public static String formatDateTime(Object obj) {
        return obj != null ? dateTimeFormat.format(obj) : "";
    }

    public static String formatLongDateTime(Object obj) {
        return obj != null ? dateTimeLongFormat.format(obj) : "";
    }

    public static String convertLongToDateTime(Long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dt = new Date(time * 1000L);
        String sDateTime = sdf.format(dt);
        return sDateTime;
    }

    public static Long formatStrDateTime2Long(String dateTime, String format) throws Exception {
        if (StringUtils.isBlank(dateTime)) {
            throw new Exception("日期时间不能为空!");
        } else if (StringUtils.isBlank(format)) {
            throw new Exception("日期格式不能为空!");
        } else {
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date date = null;

            try {
                date = df.parse(dateTime);
            } catch (ParseException var5) {
                throw new Exception(var5.toString());
            }

            return date.getTime() / 1000L;
        }
    }

    public static void main(String[] args) {
        String dateTime = formatLongDateTime(new Date());

        try {
            Long time = formatStrDateTime2Long(dateTime, "yyyy-MM-dd HH:mm:ss");
            System.out.println(formatLongDateTime(new Date(time)));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    static {
        dateTimeFormat = new SimpleDateFormat(dateFormatStr);
        dateLongFormatStr = dateFormatStr + " HH:mm:ss";
        dateTimeLongFormat = new SimpleDateFormat(dateLongFormatStr);
    }
}
