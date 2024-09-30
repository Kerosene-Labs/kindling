package com.kerosenelabs.kindling.handler;

import com.kerosenelabs.kindling.HttpRequest;
import com.kerosenelabs.kindling.HttpResponse;
import com.kerosenelabs.kindling.constant.HttpMethod;
import com.kerosenelabs.kindling.constant.HttpStatus;
import com.kerosenelabs.kindling.exception.KindlingException;

public abstract class RequestHandler {
    public abstract HttpResponse handle(HttpRequest httpRequest) throws KindlingException;

    public abstract boolean accepts(HttpRequest httpRequest) throws KindlingException;

    /**
     * Called from {@link com.kerosenelabs.kindling.KindlingServer} if an error
     * occurs during
     * {@link RequestHandler#handle(HttpRequest)}
     * 
     * @param t the Throwable that occurred
     * @return
     */
    public HttpResponse handleError(Throwable t) {
        return new HttpResponse.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .content("Internal Server Error")
                .build();
    }
}