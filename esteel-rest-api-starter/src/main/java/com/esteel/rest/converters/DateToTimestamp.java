package com.esteel.rest.converters;

import org.springframework.core.convert.converter.Converter;

import java.util.Date;

import static com.esteel.common.utils.DateUtils.getTimeLong;

public class DateToTimestamp implements Converter<Date, Long> {

  @Override
  public Long convert(Date source) {
    return getTimeLong(source);
  }
}
