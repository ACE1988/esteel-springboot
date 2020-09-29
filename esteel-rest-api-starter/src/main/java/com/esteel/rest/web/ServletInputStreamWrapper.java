package com.esteel.rest.web;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ServletInputStreamWrapper extends ServletInputStream {

    private ByteArrayInputStream sourceStream;

    public ServletInputStreamWrapper(byte[] body) {
        this.sourceStream = new ByteArrayInputStream(body);
    }

    @Override
    public boolean isFinished() {
        return sourceStream.available() <= 0;
    }

    @Override
    public boolean isReady() {
        return sourceStream.available() > 0;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
        return sourceStream.read();
    }

    @Override
    public void close() throws IOException {
        super.close();
        sourceStream.close();
    }
}
