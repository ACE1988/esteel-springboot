package com.esteel.common.mail;

import java.io.InputStream;

/**
 * @version 1.0.0
 * @ClassName MailAttachment.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日  13:29
 */
public class MailAttachment {

    private String fileName;
    private InputStream inputStream;

    public MailAttachment(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
