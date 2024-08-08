package io.kerosenelabs.kindling;

import java.util.HashMap;

import io.kerosenelabs.kindling.exception.KindlingException;

public class HttpResponse {
    private int statusCode;
    private HashMap<String, String> headers = new HashMap<>();
    private String content;

    HttpResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.headers = builder.headers;
        this.content = builder.content;
    }

    public static class Builder {
        private int statusCode;
        private HashMap<String, String> headers = new HashMap<>();
        private String content;

        public Builder setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder setHeaders(HashMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder setContent(String content) {
            return this;
        }

        public HttpResponse build() throws KindlingException {
            // evaluate nulls
            if (headers == null) {
                throw new KindlingException("Headers in response must not be null");
            }
            return new HttpResponse(this);
        }
    }
}
