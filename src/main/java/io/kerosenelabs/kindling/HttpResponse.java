package io.kerosenelabs.kindling;

import java.util.HashMap;

import io.kerosenelabs.kindling.constant.HttpStatus;

public class HttpResponse {
    private HttpStatus httpStatus;
    private HashMap<String, String> headers = new HashMap<>();
    private String content;

    HttpResponse(Builder builder) {
        this.httpStatus = builder.httpStatus;
        this.headers = builder.headers;
        this.content = builder.content;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(String.format("HTTP1/1 %s %s\r\n", httpStatus.getNumber(), httpStatus.getDescription()));

        // iterate over each header, add it
        for (HashMap.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        }

        // add the separator
        stringBuilder.append("\r\n");

        // add the content
        stringBuilder.append(content);

        return stringBuilder.toString();
    }

    public static class Builder {
        private HttpStatus httpStatus;
        private HashMap<String, String> headers = new HashMap<>();
        private String content;

        public Builder status(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder headers(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder content(String content) {
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
