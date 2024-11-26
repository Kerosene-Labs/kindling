package com.kerosenelabs.kindling;

import java.util.HashMap;

import com.kerosenelabs.kindling.constant.HttpStatus;
import com.kerosenelabs.kindling.constant.MimeType;
import com.kerosenelabs.kindling.exception.KindlingException;

public class HttpResponse {
    private HttpStatus httpStatus;
    private HashMap<String, String> headers = new HashMap<>();
    private String content;
    private MimeType contentType;

    HttpResponse(Builder builder) throws KindlingException {
        this.httpStatus = builder.httpStatus;
        this.headers = builder.headers;
        this.content = builder.content;
        this.contentType = builder.contentType;

        // implicitly calculate Content-Length
        if (content != null) {
            headers.put("Content-Length", Integer.toString(content.getBytes().length));
        }

        // check if the content-type header has already been set
        for (String key : headers.keySet()) {
            if (key.toLowerCase().equals("content-type") && this.contentType != null) {
                throw new KindlingException(
                        "Programming error, you're trying to set the 'Content-Type' header manually AND set it through 'HttpResponse.Builder'");
            }
        }
        if (contentType == null) {
            throw new KindlingException("Programming error, you must specify a Content");
        }
        headers.put("Content-Type", contentType.getMimeType());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(String.format("HTTP/1.1 %s %s\r\n", httpStatus.getNumber(), httpStatus.getDescription()));

        // iterate over each header, add it
        for (HashMap.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        }

        // add the separator
        stringBuilder.append("\r\n");

        // add the content
        if (content != null) {
            stringBuilder.append(content);
        }

        return stringBuilder.toString();
    }

    public static class Builder {
        private HttpStatus httpStatus;
        private HashMap<String, String> headers = new HashMap<>();
        private String content;
        private MimeType contentType;

        public Builder status(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder contentType(MimeType contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpResponse build() throws KindlingException {
            return new HttpResponse(this);
        }
    }
}
