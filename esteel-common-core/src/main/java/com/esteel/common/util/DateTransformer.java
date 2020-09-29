package com.esteel.common.util;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @version 1.0.0
 * @ClassName DateTransformer.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
public class DateTransformer {

    public static final String DATE_SHORT_FORMAT = "yyyyMMdd";
    public static final String DATE_TIME_SHORT_FORMAT = "yyyyMMddHHmm";
    public static final String DATE_TIMESTAMP_SHORT_FORMAT = "yyyyMMddHHmmss";
    public static final String DATE_TIMESTAMP_LONG_FORMAT = "yyyyMMddHHmmssS";
    public static final String DATE_CH_FORMAT = "yyyy年MM月dd日";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String TIME_SHORT_FORMAT = "HHmmss";

    private DateTransformer() {}

    public static Date addDays(Date date, long days) {
        if (date == null) {
            return null;
        }
        if (days == 0) {
            return date;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime newDateTime = localDateTime.plusDays(days);
        return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime of(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static Date of(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);

        return Date.from(zdt.toInstant());
    }

    public static Date of(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static Date parse(String format, String text) {
        if (StringUtils.isBlank(format) || StringUtils.isBlank(text)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(text, formatter);
        return of(localDateTime);
    }

    public static String format(String format, Date date) {
        if (StringUtils.isBlank(format) || date == null) {
            return null;
        }

        return DateTimeFormatter.ofPattern(format).format(of(date));
    }

    public static boolean sameDay(Date d1, Date d2) {
        Date day1 = day(d1);
        Date day2 = day(d2);
        return day1 == null && day2 == null || (day1 == null || day2 != null) && day1 != null && day1.compareTo(day2) == 0;
    }

    public static int dayOfMonth(Date date) {
        if (date == null) {
            return -1;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfMonth();
    }

    public static int getMonth(Date date) {
        if (date == null) {
            return -1;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getMonthValue();
    }

    public static int getYear(Date date) {
        if (date == null) {
            return -1;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getYear();
    }

    public static Date day(Date date) {
        ZonedDateTime zonedDateTimeStart = startOfDayTime(date);
        return zonedDateTimeStart == null ? null : Date.from(zonedDateTimeStart.toInstant());
    }

    public static Date endOfDay(Date date) {
        ZonedDateTime zonedDateTimeStart = startOfDayTime(date);
        if (zonedDateTimeStart == null) {
            return null;
        }
        Instant instant = zonedDateTimeStart.
                plus(1, ChronoUnit.DAYS).
                minus(1, ChronoUnit.MILLIS).
                toInstant();
        return Date.from(instant);
    }

    public static Date today() {
        return day(new Date());
    }

    public static int years(Date from, Date to) {
        if (from == null || to == null) {
            return -1;
        }
        Period period = period(from, to);
        return period.getYears();
    }

    public static int months(Date from, Date to) {
        if (from == null || to == null) {
            return -1;
        }
        Period period = period(from, to);
        return period.getMonths();
    }

    public static int days(Date from, Date to) {
        if (from == null || to == null) {
            return -1;
        }
        Period period = period(from, to);
        return period.getDays();
    }

    public static long hours(Date from, Date to) {
        if (from == null || to == null) {
            return -1;
        }
        LocalDateTime l1 = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime l2 = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ChronoUnit.HOURS.between(l1, l2);
    }

    public static long minutes(Date from, Date to) {
        if (from == null || to == null) {
            return -1;
        }
        LocalDateTime l1 = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime l2 = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return ChronoUnit.MINUTES.between(l1, l2);
    }

    private static ZonedDateTime startOfDayTime(Date date) {
        if (date == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), zoneId);
        return zonedDateTime.toLocalDate().atStartOfDay(zoneId);
    }

    private static Period period(Date from, Date to) {
        LocalDate l1 = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate l2 = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(l1, l2);
    }

    private static Duration duration(Date from, Date to) {
        LocalDateTime l1 = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime l2 = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Duration.between(l1, l2);
    }
}
