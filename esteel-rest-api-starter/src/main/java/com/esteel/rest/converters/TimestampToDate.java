package com.esteel.rest.converters;


import com.esteel.common.util.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class TimestampToDate implements Converter<String, Date> {
  @Override
  public Date convert(String source) {
    if (StringUtils.isBlank(source)) {
      return null;
    }
    if (!NumberUtil.isNumerical(source.trim())) {
      return null;
    }
    return new Date(Long.decode(source));

  }
}
