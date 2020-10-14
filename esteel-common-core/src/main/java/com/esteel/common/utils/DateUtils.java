package com.esteel.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.esteel.common.utils.LocalDateTimeUtils.*;

/**
 * @version 1.0.0
 * @ClassName DateUtils.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

/**
 * 重要说明
 * 此处书写方法时 请调用 LocalDateTimeUtils类中的方法 或 直接在LocalDateTimeUtils类中实现
 * 本类只做类型转换和格式化
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

  protected final static Logger logger = LoggerFactory.getLogger(DateUtils.class);


  public static final String DATE_SHORT_FORMAT = "yyyyMMdd";
  public static final String DATE_YEAR_SHORT_FORMAT = "yyMMddHHmmss";
  public static final String DATE_TIME_SHORT_FORMAT = "yyyyMMddHHmm";
  public static final String DATE_TIMESTAMP_SHORT_FORMAT = "yyyyMMddHHmmss";
  public static final String DATE_TIMESTAMP_LONG_FORMAT = "yyyyMMddHHmmssS";
  public static final String DATE_CH_FORMAT = "yyyy年MM月dd日";

  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String TIME_FORMAT = "HH:mm:ss";
  public static final String TIME_SHORT_FORMAT = "HHmmss";

  public static final String DAYTIME_START = "00:00:00";
  public static final String DAYTIME_END = "23:59:59";

  private DateUtils() {
  }

  private static final String[] FORMATS = {
      "yyyy-MM-dd",
      "yyyy-MM-dd HH:mm",
      "yyyy-MM-dd HH:mm:ss",
      "yyyy-MM-dd HH:mm:ss.S",
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
   * 推荐使用 getFormatDateTime(Object date)
   *
   * @param str
   * @return
   */
  @Deprecated
  public static Date convert(String str) {
    if (str != null && str.length() > 0) {
      if (str.length() > 10 && str.charAt(10) == 'T') {
        str = str.replace('T', ' '); // 去掉json-lib加的T字母
      }
      for (String format : FORMATS) {
        if (str.length() == format.length()) {
          Date date = getDate(str, format);
          if (date != null) {return date ;};
        }
      }
    }
    return null;
  }

  /**
   * getFormatDateTime(Object date, String format)
   *
   * @param str
   * @param format
   * @return
   */
  @Deprecated
  public static Date convert(String str, String format) {
    if (!StringUtils.isEmpty(str)) {
      return getDate(str, format);
    }
    return null;
  }

  private static Date getDate(String str, String format) {
    try {
      return new SimpleDateFormat(format).parse(str);
    } catch (ParseException e) {
      if (logger.isWarnEnabled()) {
        logger.warn(e.getMessage(), e);
      }
    }
    return null;
  }

  /**
   * 时间拼接 将日期和实现拼接 ymd 如2012-05-15 hm 如0812
   *
   * @date 2012-11-22 下午4:48:43
   */
  public static Date concat(String ymd, String hm) {
    if (!StringUtils.isEmpty(ymd) && !StringUtils.isEmpty(hm)) {
      try {
        String dateString = ymd.concat(" ").concat(
            hm.substring(0, 2).concat(":").concat(hm.substring(2, 4)).concat(":00"));
        Date date = DateUtils.convert(dateString, DateUtils.DATE_TIME_FORMAT);
        return date;
      } catch (NullPointerException e) {
        if (logger.isWarnEnabled()) {
          logger.warn(e.getMessage(), e);
        }
      }
    }
    return null;
  }

  /**
   * 推荐使用 getFormatDateTime(Date date, String DATE_SHORT_FORMAT)
   * <p>
   * 根据传入的日期返回年月日的6位字符串，例：20101203
   *
   * @date 2012-11-28 下午8:35:55
   */
  @Deprecated
  public static String getDay(Date date) {
    return convert(date, DATE_SHORT_FORMAT);
  }

  /**
   * 推荐使用 getFormatDateTime(Date date, String DATE_CH_FORMAT)
   * <p>
   * 根据传入的日期返回中文年月日字符串，例：2010年12月03日
   *
   * @date 2012-11-28 下午8:35:55
   */
  @Deprecated
  public static String getChDate(Date date) {
    return convert(date, DATE_CH_FORMAT);
  }

  /**
   * 将传入的时间格式的字符串转成时间对象
   * <p>
   * 例：传入2012-12-03 23:21:24
   *
   * @date 2012-11-29 上午11:29:31
   */
  public static Date strToDate(String dateStr) {
    SimpleDateFormat formatDate = new SimpleDateFormat(DATE_TIME_FORMAT);
    Date date = null;
    try {
      date = formatDate.parse(dateStr);
    } catch (Exception e) {

    }
    return date;
  }

  public static String convert(Date date) {
    return convert(date, DATE_TIME_FORMAT);
  }

  public static String convert(Date date, String dateFormat) {
    if (date == null) {
      return null;
    }

    if (null == dateFormat) {
      dateFormat = DATE_TIME_FORMAT;
    }

    return new SimpleDateFormat(dateFormat).format(date);
  }

  /**
   * 推荐使用 getStartDateTime(Object date, long 0L)
   * <p>
   * 返回该天从00:00:00开始的日期
   *
   * @param date
   * @return
   */
  @Deprecated
  public static Date getStartDatetime(Date date) {
    String thisdate = convert(date, DATE_FORMAT);
    return convert(thisdate + " " + DAYTIME_START);

  }

  /**
   * 推荐使用 getStartDateTime(Object date, long 0L)
   * <p>
   * 返回n天后从00:00:00开始的日期
   *
   * @param date
   * @return
   */
  @Deprecated
  public static Date getStartDatetime(Date date, Integer diffDays) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
    String thisdate = df.format(date.getTime() + 1000l * 24 * 60 * 60 * diffDays);
    return convert(thisdate + " " + DAYTIME_START);
  }

  /**
   * 推荐使用 getEndDateTime(Object date, long 0L)
   * <p>
   * 返回该天到23:59:59结束的日期
   *
   * @param date
   * @return
   */
  @Deprecated
  public static Date getEndDatetime(Date date) {
    String thisdate = convert(date, DATE_FORMAT);
    return convert(thisdate + " " + DAYTIME_END);

  }

  /**
   * 推荐使用 getEndDateTime(Object date, long 0L)
   * <p>
   * 返回n天到23:59:59结束的日期
   *
   * @param date
   * @return
   */
  @Deprecated
  public static Date getEndDatetime(Date date, Integer diffDays) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
    String thisdate = df.format(date.getTime() + 1000L * 24 * 60 * 60 * diffDays);
    return convert(thisdate + " " + DAYTIME_END);

  }

  /**
   * 推荐使用 getEndDateTime(Object date, long 0L)
   * <p>
   * 返回该日期的最后一刻，精确到纳秒
   *
   * @return
   */
  @Deprecated
  public static Timestamp getLastEndDatetime(Date endTime) {
    Timestamp ts = new Timestamp(endTime.getTime());
    ts.setNanos(999999999);
    return ts;
  }

  /**
   * addOrMinusDateTime(Object date, long diff, ChronoUnit ChronoUnit.SECONDS)
   * <p>
   * 返回该日期加1秒
   *
   * @return
   */
  @Deprecated
  public static Timestamp getEndTimeAdd(Date endTime) {
    Timestamp ts = new Timestamp(endTime.getTime());
    Calendar c = Calendar.getInstance();
    c.setTime(ts);
    c.add(Calendar.MILLISECOND, 1000);
    c.set(Calendar.MILLISECOND, 0);
    return new Timestamp(c.getTimeInMillis());
  }

  /**
   * 推荐使用 addOrMinus(Object date, long diff, ChronoUnit ChronoUnit.DAYS)
   * 另外做类型转换
   * <p>
   * 相对当前日期，增加或减少天数
   *
   * @param date
   * @param day
   * @return
   */
  @Deprecated
  public static String addDay(Date date, int day) {
    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);

    return df.format(addDayToDate(date, day));
  }

  /**
   * 推荐使用 addOrMinusDateTime(Object date, long diff, ChronoUnit ChronoUnit.DAYS)
   * <p>
   * <p>
   * 相对当前日期，增加或减少天数
   *
   * @param date
   * @param day
   * @return
   */
  @Deprecated
  public static Date addDayToDate(Date date, long day) {
    return new Date(date.getTime() + 1000 * 24 * 60 * 60 * day);
  }

  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.DAYS)
   * <p>
   * 返回两个时间的相差天数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差天数
   */
  @Deprecated
  public static Long getTimeDiff(String startTime, String endTime) {
    return getDayDiff(startTime, endTime);
  }

  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.DAYS)
   * <p>
   * 返回两个时间的相差天数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差天数
   */
  @Deprecated
  public static Long getTimeDiff(Date startTime, Date endTime) {
    return getDayDiff(startTime, endTime);
  }

  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.DAYS)
   * <p>
   * 返回两个时间的相差天数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差天数
   */
  @Deprecated
  public static Long getDayDiff(String startTime, String endTime) {
    Long days = null;
    Date startDate = null;
    Date endDate = null;
    try {
      if (startTime.length() == 10 && endTime.length() == 10) {
        startDate = new SimpleDateFormat(DATE_FORMAT).parse(startTime);
        endDate = new SimpleDateFormat(DATE_FORMAT).parse(endTime);
      } else {
        startDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse(startTime);
        endDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse(endTime);
      }

      days = getDayDiff(startDate, endDate);
    } catch (ParseException e) {
      if (logger.isWarnEnabled()) {
        logger.warn(e.getMessage());
      }
      days = null;
    }
    return days;
  }

  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.DAYS)
   * <p>
   * 返回两个时间的相差天数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差天数
   */
  @Deprecated
  public static Long getDayDiff(Date startTime, Date endTime) {
    if (startTime == null || endTime == null) {
      return null;
    }
    Long days = null;

    Calendar c = Calendar.getInstance();
    c.setTime(startTime);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    long l_s = c.getTimeInMillis();
    c.setTime(endTime);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    long l_e = c.getTimeInMillis();
    days = (l_e - l_s) / 86400000;
    return days;
  }


  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.MINUTES)
   * <p>
   * 返回两个时间的相差分钟数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差分钟数
   */
  @Deprecated
  public static Long getMinuteDiff(Date startTime, Date endTime) {
    Long minutes = null;

    Calendar c = Calendar.getInstance();
    c.setTime(startTime);
    long l_s = c.getTimeInMillis();
    c.setTime(endTime);
    long l_e = c.getTimeInMillis();
    minutes = (l_e - l_s) / (1000L * 60);
    return minutes;
  }

  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.SECONDS)
   * <p>
   * 返回两个时间的相差秒数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差秒数
   */
  @Deprecated
  public static Long getSecondDiff(Date startTime, Date endTime) {
    Long minutes = null;
    Calendar c = Calendar.getInstance();
    c.setTime(startTime);
    long l_s = c.getTimeInMillis();
    c.setTime(endTime);
    long l_e = c.getTimeInMillis();
    minutes = (l_e - l_s) / 1000L;
    return minutes;
  }

  /**
   * 推荐使用 diff(Object start, Object end, ChronoUnit ChronoUnit.SECONDS)
   * <p>
   * 返回两个时间的相差秒数
   *
   * @param startTime 对比的开始时间
   * @param endTime   对比的结束时间
   * @return 相差秒数
   */
  @Deprecated
  public static Long getSecondDiff(String startTime, String endTime) {
    Long seconds = null;
    Date startDate = null;
    Date endDate = null;
    try {
      if (startTime.length() == 10 && endTime.length() == 10) {
        startDate = new SimpleDateFormat(DATE_FORMAT).parse(startTime);
        endDate = new SimpleDateFormat(DATE_FORMAT).parse(endTime);
      } else {
        startDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse(startTime);
        endDate = new SimpleDateFormat(DATE_TIME_FORMAT).parse(endTime);
      }

      seconds = getSecondDiff(startDate, endDate);
    } catch (ParseException e) {
      if (logger.isWarnEnabled()) {
        logger.warn(e.getMessage());
      }
      seconds = null;
    }
    return seconds;
  }

  public static String getPidFromDate(Date date) {
    if (date == null) {
      return "";
    }

    String m = convert(date, "yyyyMM");
    String d = convert(date, "dd");

    if (Integer.valueOf(d) <= 10) {
      d = "01";
    } else if (Integer.valueOf(d) <= 20) {
      d = "02";
    } else {
      d = "03";
    }

    return m.concat(d);
  }

  /**
   * 返回 n 天后的日期的 当天开始时间 Timestamp 继承自 Date
   *
   * @param date
   * @param n
   * @return
   */
  public static Timestamp getStartDateTime(Object date, long n) {
    return localDateTime2Timestamp(getStartLocalDateTime(date, n));
  }

  /**
   * 返回 n 天后的日期的 当天结束时间
   *
   * @param date
   * @param n
   * @return
   */
  public static Timestamp getEndDateTime(Object date, long n) {
    return localDateTime2Timestamp(getEndLocalDateTime(date, n));
  }

  /**
   * 判断一个日期 是否处于某个时钟时间段内
   * @param date      2017-10-24 10:00:00
   * @param clock1    00:00:00 or 00:00
   * @param clock2    12:00:00 or 12:00
   * @return          true
   */
  public static Boolean isBetween(Object date, String clock1, String clock2) {
    LocalTime time = toLocalDateTime(date).toLocalTime();
    return time.isAfter(LocalTime.parse(clock1)) && time.isBefore(LocalTime.parse(clock2))
        || time.isBefore(LocalTime.parse(clock1)) && time.isAfter(LocalTime.parse(clock2));
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
    return LocalDateTimeUtils.diff(start, end, unit);
  }

  /**
   * 增加或减少 多少天
   * Timestamp 继承自 Date
   *
   * @param date
   * @param diff
   * @return
   */
  public static Timestamp addOrMinusDateTime(Date date, long diff) {
    return addOrMinusDateTime(date, diff, ChronoUnit.DAYS);
  }

  /**
   * 增加或减少 多少 年/月/日/时/分/秒/毫秒/微秒/纳秒
   *
   * @param date
   * @param diff
   * @param unit
   * @return
   */
  public static Timestamp addOrMinusDateTime(Object date, long diff, ChronoUnit unit) {
    return localDateTime2Timestamp(addOrMinusLocalDateTime(date, diff, unit));
  }

  /**
   * 对象 转 Date
   *
   * @param date
   * @return
   */
  public static Date obj2Date(Object date) {
    return localDateTime2Date(toLocalDateTime(date));
  }

  /**
   * 对象 转指定格式 Date日期
   *
   * @param date
   * @param format
   * @return
   */
  public static Date obj2Date(Object date, String format) {
    return localDateTime2Date(toLocalDateTime(getFormatDateTime(date, format)));
  }

  /**
   * 对象 转默认格式 String日期
   *
   * @param date
   * @return
   */
  public static String getFormatDateTime(Object date) {
    return getFormatDateTime(date, DATE_TIME_FORMAT);
  }

  /**
   * 对象 转指定格式 String日期
   *
   * @param date
   * @param format
   * @return
   */
  public static String getFormatDateTime(Object date, String format) {
    return localDateTime2Str(toLocalDateTime(date), format);
  }


  public static long getTimeLong(Date date){
    return Objects.nonNull(date)?date.getTime():0;
  }
}


