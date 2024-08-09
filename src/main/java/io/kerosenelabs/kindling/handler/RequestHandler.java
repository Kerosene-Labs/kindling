package io.kerosenelabs.kindling.handler;

import io.kerosenelabs.kindling.HttpRequest;
import io.kerosenelabs.kindling.HttpResponse;
import io.kerosenelabs.kindling.constant.HttpMethod;
import io.kerosenelabs.kindling.constant.HttpStatus;
import io.kerosenelabs.kindling.exception.KindlingException;

public abstract class RequestHandler {
    public abstract HttpResponse handle(HttpRequest httpRequest) throws KindlingException;

    public abstract boolean accepts(HttpRequest httpRequest) throws KindlingException;

    /**
     * Called from {@link io.kerosenelabs.kindling.KindlingServer} if an error occurs during
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