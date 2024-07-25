package com.buy_customers.common.config.api;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class CachedBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;
    private boolean isMultipart;

    public CachedBodyHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.isMultipart = isMultipartContent(request);
        if (!isMultipart) {
            InputStream requestInputStream = request.getInputStream();
            this.cachedBody = toByteArray(requestInputStream);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (isMultipart) {
            return super.getRequest().getInputStream();
        }
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (isMultipart) {
            return super.getRequest().getReader();
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream, StandardCharsets.UTF_8));
    }

    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    private boolean isMultipartContent(HttpServletRequest request) {
        return request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/");
    }

    private class CachedBodyServletInputStream extends ServletInputStream {

        private ByteArrayInputStream buffer;

        public CachedBodyServletInputStream(byte[] contents) {
            this.buffer = new ByteArrayInputStream(contents);
        }

        @Override
        public boolean isFinished() {
            return buffer.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // No operation
        }

        @Override
        public int read() throws IOException {
            return buffer.read();
        }
    }
}
