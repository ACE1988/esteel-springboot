package com.esteel.rest;

import org.springframework.web.server.ServerWebExchange;

import java.security.Principal;

public interface ReturnValueConverter<T> {

    int getOrder();

    boolean match(ServerWebExchange exchange, Object returnValue);

    T convert(ServerWebExchange exchange, Object returnValue, Principal principal);
}
