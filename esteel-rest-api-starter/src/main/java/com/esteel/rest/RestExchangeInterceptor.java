package com.esteel.rest;

import org.springframework.web.server.ServerWebExchange;

public interface RestExchangeInterceptor {

    String[] getPathPatterns();

    void afterCompletion(ServerWebExchange exchange);
}
