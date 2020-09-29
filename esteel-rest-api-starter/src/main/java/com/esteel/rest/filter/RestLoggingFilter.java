package com.esteel.rest.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.esteel.rest.web.ContentCachingRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;

import static com.esteel.rest.web.ContentCachingRequestWrapper.TRACE_KEY;


@Slf4j
public class RestLoggingFilter extends OncePerRequestFilter {

    private static final String FIELD_SEPARATOR = ":";
    private static final String LINE_SEPARATOR = " ";

    private final LongAdder counter = new LongAdder();

    private Set<String> ignoreTracePathPrefix;

    public void setIgnoreTracePathPrefix(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            String[] splits = prefix.split(",");
            ignoreTracePathPrefix = Sets.newHashSet(splits);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        if (!needTrace(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            initializeMDC(requestWrapper);
            traceRequest(requestWrapper);
            filterChain.doFilter(requestWrapper, responseWrapper);
            traceResponse(responseWrapper, requestWrapper.getInterval());
        } finally {
            MDC.clear();
        }
    }

    private boolean needTrace(HttpServletRequest request) {
        String uri = request.getRequestURI();
        boolean matchIgnorePath = false;
        if (ignoreTracePathPrefix != null) {
            matchIgnorePath = ignoreTracePathPrefix.stream().anyMatch(uri::startsWith);
        }
        return !uri.startsWith("/api/swagger") &&
                !uri.startsWith("/api/v2/api-docs") &&
                !uri.startsWith("/api/webjars") &&
                !uri.startsWith("/webjars") &&
                !uri.startsWith("/swagger") &&
                !uri.startsWith("/v2/api-docs") &&
                !matchIgnorePath;
    }

    private void traceRequest(ContentCachingRequestWrapper request) {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        StandardServletMultipartResolver multipartResolver  = new StandardServletMultipartResolver();
        String value = "";
        String contentType = request.getContentType();
        if (multipartResolver.isMultipart(request)||"image/jpeg".equalsIgnoreCase(contentType)) {//有文件上传
            //将request变成多部分request


            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("contentType :").append(contentType).append(",");
            stringBuffer.append("ContentLength :").append(request.getContentLengthLong()).append(",");
            value = stringBuffer.toString();
        } else {
            byte[] body = request.getContentAsByteArray();
            value = (body == null || body.length == 0) ? request.getQueryString() : new String(body);
        }
        String authorization = request.getHeader("Authorization");
        String authorities = getAuthorities(SecurityContextHolder.getContext().getAuthentication());
        log.info("=> HTTP>REQ: |{}|{}|{}",
                StringUtils.isBlank(value) ? "" : value,
                StringUtils.isBlank(authorization) ? "" : authorization,
                StringUtils.isBlank(authorities) ? "" : authorities);
    }

    private void traceResponse(ContentCachingResponseWrapper response, long interval) {
        String contentType = response.getContentType();
            int statusCode = response.getStatusCode();
            String headers = getResponseHeaders(response);
            byte[] body = new byte[0];
            if(!StringUtils.startsWithAny(contentType,"multipart","image")) {
                body = response.getContentAsByteArray();
                try {
                    response.copyBodyToResponse();
                } catch (IOException e) {
                    if (!(e instanceof org.apache.catalina.connector.ClientAbortException)) {
                        log.error("Copy body to response error.", e);
                    }
                }
            }
            log.info("=> HTTP>RES {}: {} {} {}", interval, statusCode, headers, new String(body));

    }

    private void initializeMDC(ContentCachingRequestWrapper request) {
        StringBuilder builder = new StringBuilder();
        buildRequestKey(builder, request);
        buildHeaders(builder, request);
        MDC.put("REQ_ID", builder.toString().trim());
    }

    private void buildRequestKey(StringBuilder builder, ContentCachingRequestWrapper request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        builder.append(getRequestId()).append(LINE_SEPARATOR).
                append(request.getMethod()).append(FIELD_SEPARATOR).
                append(request.getRequestURI());
        if (principal != null) {
            String userId = "";
            try {
                String jsonStr = String.valueOf(principal);
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                userId = jsonObject.getString("userId");
            } catch (Exception e) {
//                log.error("JSONObject.parseObject error!",e);
            }
            if (StringUtils.isBlank(userId) && !StringUtils.equalsIgnoreCase("null", userId)) {
                builder.append(LINE_SEPARATOR).append(principal);
            } else {
                builder.append(LINE_SEPARATOR).append(userId);
            }

        }
        builder.append(LINE_SEPARATOR);
    }

    private void buildHeaders(StringBuilder builder, ContentCachingRequestWrapper request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (headerName.toLowerCase().startsWith("x-")) {
                String header = request.getHeader(headerName);
                builder.append(headerName).append(": ").append(header).append(LINE_SEPARATOR);
            }
        }
    }

    private String getResponseHeaders(ContentCachingResponseWrapper response) {
        StringBuilder builder = new StringBuilder();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            String header = response.getHeader(headerName);
            builder.append(headerName).append(": ").append(header).append(LINE_SEPARATOR);
        }
        return builder.toString();
    }

    private String getRequestId() {
        long value = System.currentTimeMillis();
        String suffix = Long.toString(value + Thread.currentThread().getId() + counter.longValue(), 33);
        counter.increment();
        String id = String.format("%s%s", value, suffix).toUpperCase();
        MDC.put(TRACE_KEY, id);
        return id;
    }

    private String getAuthorities(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (builder.length() == 0) {
                builder.append(authority.getAuthority());
            } else {
                builder.append(",").append(authority.getAuthority());
            }
        }
        return builder.toString();
    }

}
