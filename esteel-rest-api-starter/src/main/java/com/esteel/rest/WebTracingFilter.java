package com.esteel.rest;

import com.esteel.rest.exchange.BodyCachingServerWebExchangeDecorator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Slf4j
public class WebTracingFilter implements WebFilter {

    private PathMatcher pathMatcher = new AntPathMatcher();

    private List<RestExchangeInterceptor> interceptors = Lists.newArrayList();

    private Set<String> ignoreTracePathPrefix;

    public void addInterceptor(RestExchangeInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    public void setIgnoreTracePathPrefix(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            String[] splits = prefix.split(",");
            ignoreTracePathPrefix = Sets.newHashSet(splits);
        }
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!traceable(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal().
                defaultIfEmpty(new AnonymousAuthenticationToken("EMPTY", new Object(), Lists.newArrayList(new SimpleGrantedAuthority("USER")))).
                flatMap(principal -> filter(exchange, chain, principal instanceof AnonymousAuthenticationToken ? null : principal)).
                doFinally(v -> {
                    for (RestExchangeInterceptor interceptor : this.interceptors) {
                        for (String pattern : interceptor.getPathPatterns()) {
                            if (pathMatcher.match(pattern, exchange.getRequest().getPath().toString())) {
                                interceptor.afterCompletion(exchange);
                            }
                        }
                    }
                });
    }

    private Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain, Principal principal) {
        BodyCachingServerWebExchangeDecorator wrapped = new BodyCachingServerWebExchangeDecorator(exchange).mdc(principal);
        exchange.getAttributes().put("WRAPPED", wrapped);
        return chain.filter(wrapped);
    }

    private boolean traceable(ServerHttpRequest request) {
        String uri = request.getPath().toString();
        boolean matchIgnorePath = false;
        if (ignoreTracePathPrefix != null) {
            matchIgnorePath = ignoreTracePathPrefix.stream().anyMatch(uri::startsWith);
        }
        return !uri.startsWith("/swagger") &&
                !uri.startsWith("/webjars") &&
                !uri.startsWith("/v2/api-docs") &&
                !matchIgnorePath;
    }
}
