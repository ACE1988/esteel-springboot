package com.esteel.rest;

import com.esteel.rest.common.RestResponse;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

import static com.esteel.rest.common.RestResponse.ok;
import static org.springframework.core.ResolvableType.forClass;

public class RestBodyConverter implements ReturnValueConverter<RestResponse> {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean match(ServerWebExchange exchange, Object returnValue) {
        return !noNeedWrap(returnValue.getClass());
    }

    @Override
    public RestResponse convert(ServerWebExchange exchange, Object returnValue, Principal principal) {
        return ok(returnValue);
    }

    private boolean noNeedWrap(Class<?> type) {
        return noNeedWrap(forClass(type));
    }

    private boolean noNeedWrap(ResolvableType resolvableType) {
        boolean already = forClass(RestResponse.class).isAssignableFrom(resolvableType);
        boolean mono = forClass(Mono.class).isAssignableFrom(resolvableType);
        boolean bytes = forClass(byte[].class).isAssignableFrom(resolvableType);
        boolean resource = forClass(Resource.class).isAssignableFrom(resolvableType);

        return (already || mono || bytes || resource);
    }
}
