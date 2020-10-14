package com.esteel.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @version 1.0.0
 * @ClassName WebUtil.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */

@Slf4j
public class WebUtil {

    private static String BEARER_TYPE = "Bearer";

    private WebUtil() {}

    public static String join(String root, String path) {
        if (StringUtils.isBlank(path)) {
            return root;
        }
        String full = root;
        if (!full.endsWith("/")) {
            full = full.concat("/");
        }

        return path.startsWith("/") ? full.concat(path.substring(1, path.length())) : full.concat(path);
    }

    public static String extractToken(String value) {
        if ((value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
            String authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
            int commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }
        return null;
    }

    public static InputStream getInputStream(String spec) {
        try {
            URL url = new URL(spec);
            return url.openStream();
        } catch (IOException e) {
            log.warn("Open stream for {} failed: {}", spec, e.getMessage());
            return null;
        }
    }
}
