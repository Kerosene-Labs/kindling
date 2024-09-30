package com.kerosenelabs.kindling.dto;

import com.kerosenelabs.kindling.constant.HttpMethod;

public class HttpRequestHead {
    private HttpMethod httpMethod;
    private String resource;
    private String protocol;

    public HttpRequestHead(HttpMethod httpMethod, String resource, String protocol) {
        this.httpMethod = httpMethod;
        this.resource = resource;
        this.protocol = protocol;
    }

    public HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public String getResource() {
        return this.resource;
    }

    public String getProtocol() {
        return this.protocol;
    }
}
