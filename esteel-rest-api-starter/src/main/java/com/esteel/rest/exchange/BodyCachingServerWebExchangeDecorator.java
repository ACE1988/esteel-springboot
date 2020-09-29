package com.esteel.rest.exchange;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BodyCachingServerWebExchangeDecorator extends ServerWebExchangeDecorator {

    static final String LINE_SEPARATOR = " ";

    private static final String FIELD_SEPARATOR = ":";

    private ServerHttpRequest request;
    private ServerHttpResponse response;

    public BodyCachingServerWebExchangeDecorator(ServerWebExchange delegate) {
        super(delegate);
    }

    public BodyCachingServerWebExchangeDecorator mdc(Principal principal) {
        mdc(getDelegate(), principal);

        this.request = new BodyCachingServerHttpRequestDecorator(getDelegate().getRequest());
        this.response = new BodyCachingServerHttpResponseDecorator(getDelegate().getResponse());
        return this;
    }

    @Override
    public ServerHttpRequest getRequest() {
        return this.request;
    }

    @Override
    public ServerHttpResponse getResponse() {
        return this.response;
    }

    private static void mdc(ServerWebExchange exchange, Principal principal) {
        StringBuilder builder = new StringBuilder();
        appendRequestKey(builder, exchange, principal);
        appendHeaders(builder, exchange);
        MDC.put("REQ_ID", builder.toString().trim());
    }

    private static void appendRequestKey(StringBuilder builder, ServerWebExchange exchange, Principal principal) {
        ServerHttpRequest request = exchange.getRequest();
        builder.append(exchange.getLogPrefix()).append(LINE_SEPARATOR).
                append(request.getMethod()).append(FIELD_SEPARATOR).
                append(request.getPath());
        if (principal != null) {
            String name = principal.getName();
            builder.append(LINE_SEPARATOR).append(name);
            if (principal instanceof Authentication) {
                String roles = ((Authentication) principal).getAuthorities().stream().
                        map(GrantedAuthority::getAuthority).
                        collect(Collectors.joining(","));
                builder.append("|").append(roles);
            }
        }
        builder.append(LINE_SEPARATOR);
    }

    private static void appendHeaders(StringBuilder builder, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            if (headerName.toLowerCase().startsWith("x-")) {
                String header = String.join(",", entry.getValue());
                builder.append(headerName).append(": ").append(header).append(LINE_SEPARATOR);
            }
        }
    }
}
