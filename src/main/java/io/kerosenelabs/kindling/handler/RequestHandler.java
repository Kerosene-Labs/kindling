package io.kerosenelabs.kindling.handler;

import java.net.http.HttpRequest;

import io.kerosenelabs.kindling.HttpResponse;
import io.kerosenelabs.kindling.exception.KindlingException;

public class RequestHandler {
    private String resource;

    public RequestHandler(String resource) {
        this.resource = resource;
    }

    public HttpResponse handle(HttpRequest httpRequest) throws KindlingException {
        return new HttpResponse.Builder().setStatusCode(200).build();
    }
}
