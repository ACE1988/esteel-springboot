package com.esteel.rest.i18n;

import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

public class MessageSourceHandler {

  @Setter
  private HttpServletRequest request;
  @Setter
  private MessageSource messageSource;

  public String getMessage(String messageKey) {
    String message = messageSource.getMessage(messageKey, null, RequestContextUtils.getLocale(request));
    return message;
  }

}
