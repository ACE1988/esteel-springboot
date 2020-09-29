package com.esteel.rest.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {

    public static final String TRACE_KEY = "TRACE_ID";

    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_ADDRESS = "127.0.0.1";

    interface Header {
        String AUTHORIZATION = "Authorization";
        String AUTHENTICATION_TYPE_BASIC = "Basic";
        String X_AUTH_TOKEN = "X-AUTH-TOKEN";
        String WWW_Authenticate = "WWW-Authenticate";
        String X_REAL_IP = "X-Real-IP";
        String X_FORWARDED_FOR = "X-Forwarded-For";
        String PROXY_CLIENT_IP = "Proxy-Client-IP";
        String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
        String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
        String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    }

    private long entryTime;

    private byte[] body;

    private ServletInputStream inputStream;

    public ContentCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        this.entryTime = System.currentTimeMillis();

        this.body = StreamUtils.copyToByteArray(request.getInputStream());
        this.inputStream = new ServletInputStreamWrapper(this.body);
    }

    public long getInterval() {
        return System.currentTimeMillis() - entryTime;
    }

    public byte[] getContentAsByteArray() {
        return body;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.inputStream;
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = null;
        int tryCount = 0;

        while (!isIpFound(ip) && tryCount <= 6) {
            switch (tryCount) {
                case 0:
                    ip = request.getHeader(Header.X_REAL_IP);
                    break;
                case 1:
                    ip = request.getHeader(Header.X_FORWARDED_FOR);
                    if (StringUtils.isNotBlank(ip)) {
                        String[] ips = ip.split(",");
                        for (String value : ips) {
                            if (!UNKNOWN.equals(value)) {
                                ip = value;
                                break;
                            }
                        }
                    }
                    break;
                case 2:
                    ip = request.getHeader(Header.PROXY_CLIENT_IP);
                    break;
                case 3:
                    ip = request.getHeader(Header.WL_PROXY_CLIENT_IP);
                    break;
                case 4:
                    ip = request.getHeader(Header.HTTP_CLIENT_IP);
                    break;
                case 5:
                    ip = request.getHeader(Header.HTTP_X_FORWARDED_FOR);
                    break;
                default:
                    ip = request.getRemoteAddr();
            }

            tryCount++;
        }

        if (isLoopbackAddress(ip)) {
            ip = LOCAL_ADDRESS;
        }

        return ip;
    }

    private static boolean isIpFound(String ip) {
        return ip != null && ip.length() > 0 && !UNKNOWN.equalsIgnoreCase(ip);
    }

    private static boolean isLoopbackAddress(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isLoopbackAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
