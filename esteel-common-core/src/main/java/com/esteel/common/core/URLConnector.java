package com.esteel.common.core;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @version 1.0.0
 * @ClassName URLConnector.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:53
 */
@Slf4j
public class URLConnector {

    public static URLConnector open(String spec) {
        try {
            URLConnector connector = new URLConnector(new URL(spec));
            connector.connect();
            return connector;
        } catch (IOException e) {
            log.warn("Open connection for {} failed: {}", spec, e.getMessage());
            return null;
        }
    }

    private URL url;
    private URLConnection connection;

    private URLConnector(URL url) {
        this.url = url;
    }

    private void connect() throws IOException {
        this.connection = this.url.openConnection();
    }

    public String getResponseHeader(String name) {
        return this.connection.getHeaderField(name);
    }

    public InputStream getInputStream() {
        try {
            return this.connection.getInputStream();
        } catch (IOException e) {
            log.warn("Open stream for {} failed: {}", url.toString(), e.getMessage());
            return null;
        }
    }
}
