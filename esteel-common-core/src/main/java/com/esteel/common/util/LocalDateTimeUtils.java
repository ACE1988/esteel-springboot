package com.esteel.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @version 1.0.0
 * @ClassName LocalDateTimeUtils.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

public class LocalDateTimeUtils {
  protected final static Logger logger = LoggerFactory.getLogger(LocalDateTimeUtils.class);

  private static final String[] FORMATS = {
      "yyyy-MM-dd",
      "yyyy-MM-dd HH:mm",
      "yyyy-MM-dd HH:mm:ss",
      "yyyy-MM-dd HH:mm:ss.S",
      "yyyy/M/d H:m:s",
      "HH:mm",
      "HH:mm:ss",
      "yyyy-MM",
      "yyyyMMddHHmmss",
      "yyyy-MM-dd'T'HH:mm:ss",
      "yyyy-MM-dd'T'HH:mm:ss.SSS",
      "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
      "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS",
      "yyyy-MM-dd'T'HH:mm:ss.SSSz"
  };

  /**
   * 返回 n 天后的日期的 当天开始时间 Timestamp 继承自 Date
   *
   * @param date
   * @param n
   * @return
   */
  public static LocalDateTime getStartLocalDateTime(Object date, long n) {
    return toLocalDateTime(date).plus(n, ChronoUnit.DAYS).toLocalDate().atStartOfDay();
  }

  /**
   * 返回 n 天后的日期的 当天结束时间
   *
   * @param date
   * @param n
   * @return
   */
  public static LocalDateTime getEndLocalDateTime(Object date, long n) {
    return toLocalDateTime(date)
        .plus(n + 1, ChronoUnit.DAYS)
        .toLocalDate()
        .atStartOfDay()
        .minus(1, ChronoUnit.NANOS);
  }

  /**
   * 计算两个时间的 差值
   *
   * @param start
   * @param end
   * @param unit  返回时间单位 年/月/日/时/分/秒
   * @return
   */
  public static Long diff(Object start, Object end, ChronoUnit unit) {
    return diff(toLocalDateTime(start), toLocalDateTime(end), unit);
  }

  /**
   * 计算两个时间的 差值
   *
   * @param start
   * @param end
   * @param unit  返回时间单位 年/月/日/时/分/秒
   * @return
   */
  private static Long diff(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
    if (start == null || end == null || unit == null)
      throw new NullPointerException();
    Long diff = null;
    switch (unit) {
      case YEARS:
        diff = (long) periodBetween(start, end).getYears();
        break;
      case MONTHS:
        diff = (long) periodBetween(start, end).getYears() * 12 + periodBetween(start, end).getMonths();
        break;
      case DAYS:
        diff = Duration.between(start.toLocalDate().atStartOfDay(), end.toLocalDate().atStartOfDay()).toDays();
        break;
      case HOURS:
        diff = Duration.between(start, end).toHours();
        break;
      case MINUTES:
        diff = Duration.between(start, end).toMinutes();
        break;
      case SECONDS:
        diff = Duration.between(start, end).toMillis() / 1000;
        break;
      case MILLIS:
        diff = Duration.between(start, end).toMillis();
        break;
      case MICROS:
        diff = Duration.between(start, end).toNanos() / 1000;
        break;
      case NANOS:
        diff = Duration.between(start, end).toNanos();
        break;
      default:
        break;
    }

    return diff;
  }

  private static Period periodBetween(LocalDateTime start, LocalDateTime end) {
    return Period.between(start.toLocalDate(), end.toLocalDate());
  }

  public static LocalDateTime addOrMinusLocalDateTime(Object date, long diff, ChronoUnit unit) {
    return toLocalDateTime(date).plus(diff, unit);
  }

  public static LocalDateTime toLocalDateTime(Object date) {
    LocalDateTime ldt = null;
    if (date instanceof String)
      ldt = str2LocalDateTime((String) date);
    else if (date instanceof Timestamp)
      ldt = timestamp2LocalDateTime((Timestamp) date);
    else if (date instanceof Date)
      ldt = date2LocalDateTime((Date) date);
    else if (date instanceof Instant)
      ldt = instant2LocalDateTime((Instant) date);
    else if (date instanceof LocalDateTime)
      ldt = (LocalDateTime) date;
    else if (date instanceof LocalDate)
      ldt = ((LocalDate) date).atStartOfDay();
    else if (date instanceof Long)
      ldt = instant2LocalDateTime(Instant.ofEpochMilli((Long) date));
    return ldt;
  }

  /**
   * 时间互转 LocalDateTime -> Date
   *
   * @param ldt
   * @return
   */
  public static Date localDateTime2Date(LocalDateTime ldt) {
    return Date.from(localDateTime2Instant(ldt));
  }

  /**
   * 时间互转 LocalDateTime -> Timestamp
   *
   * @param ldt
   * @return
   */
  public static Timestamp localDateTime2Timestamp(LocalDateTime ldt) {
    return Timestamp.valueOf(ldt);
  }

  /**
   * 时间互转 LocalDateTime -> String
   *
   * @param ldt
   * @return
   */
  public static String localDateTime2Str(LocalDateTime ldt, String pattern) {
    return ldt.format(DateTimeFormatter.ofPattern(pattern));
  }

  /**
   * 时间互转 LocalDateTime -> Instant
   *
   * @param ldt
   * @return
   */
  public static Instant localDateTime2Instant(LocalDateTime ldt) {
    return Instant.from(ldt.atZone(ZoneId.systemDefault()));
  }

  /**
   * 时间互转 String -> LocalDateTime
   * 失败时，请在 FORMATS 中添加格式
   *
   * @param date
   * @return
   */
  private static LocalDateTime str2LocalDateTime(String date) {
    LocalDateTime localDateTime = null;
    for (String format : FORMATS) {
      localDateTime = str2LocalDateTime(date, format);
      if (localDateTime != null) {
        //格林尼治时间，防止丢失时区
        if (format.equals("yyyy-MM-dd'T'HH:mm:ss.SSSz")) {
          ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
          String offset = zonedDateTime.getOffset().getId();
          localDateTime = localDateTime.plusHours(Long.valueOf(offset.substring(1, 3)))
              .plusHours(Long.valueOf(offset.substring(4, 6)));
        }
        break;
      }
    }
    return localDateTime;
  }

  private static LocalDateTime str2LocalDateTime(String date, String format) {
    try {
      return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
    } catch (Exception e) {
      //logger.warn("LocalDateTime: format is not match : " + format);
    }
    try {
      return LocalDate.parse(date, DateTimeFormatter.ofPattern(format)).atStartOfDay();
    } catch (Exception e) {
      //logger.warn("LocalDate: format is not match : " + format);
    }
    try {
      return LocalDateTime.of(LocalDate.now(), LocalTime.parse(date, DateTimeFormatter.ofPattern(format)));
    } catch (Exception e) {
      //logger.warn("LocalDateTime: format is not match : " + format);
    }
    return null;
  }

  /**
   * 时间互转 Timestamp -> LocalDateTime
   *
   * @param ts
   * @return
   */
  private static LocalDateTime timestamp2LocalDateTime(Timestamp ts) {
    return ts.toLocalDateTime();
  }

  /**
   * 时间互转 Date -> LocalDateTime
   *
   * @param date
   * @return
   */
  private static LocalDateTime date2LocalDateTime(Date date) {
    return instant2LocalDateTime(date.toInstant());
  }

  /**
   * 时间互转 Instant -> LocalDateTime
   *
   * @param instant
   * @return
   */
  private static LocalDateTime instant2LocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }
}