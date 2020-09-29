package com.esteel.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.esteel.rest.common.RestResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

import static com.esteel.rest.common.RestResponse.ok;


@ControllerAdvice()
public class RestBodyAdvice implements ResponseBodyAdvice {

    private ObjectMapper OM = new ObjectMapper();

    @Override
    public Object beforeBodyWrite(Object returnValue, MethodParameter methodParameter,
                                  MediaType mediaType, Class clas, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        if (mediaType.equals(MediaType.TEXT_PLAIN)){
            return returnValue;
        }

        HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

        String uri = serverHttpRequest.getURI().toString();
        if (uri.contains("swagger") || uri.contains("api-docs")) {
            return returnValue;
        }
        if (returnValue instanceof RestResponse) {
            return returnValue;
        }
        Method method = methodParameter.getMethod();
        if (method.getDeclaredAnnotation(NotWrap.class) != null) {
            return returnValue;
        }
        if (returnValue instanceof DonotWrapContent) {
            return returnValue;
        }
        if (returnValue instanceof byte[]) {
            return returnValue;
        }
        if (returnValue == null || returnValue instanceof String) {
            try {
                return OM.writeValueAsString(ok(returnValue));
            } catch (JsonProcessingException ignored) {
            }
        }
        if (returnValue instanceof ResponseEntity) {
            return returnValue;
        }
        if (returnValue instanceof Resource) {
            return returnValue;
        }
        return ok(returnValue);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }
}
