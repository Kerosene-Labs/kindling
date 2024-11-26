package com.kerosenelabs.kindling;

import java.nio.file.Path;

import com.kerosenelabs.kindling.constant.HttpMethod;
import com.kerosenelabs.kindling.constant.HttpStatus;
import com.kerosenelabs.kindling.constant.MimeType;
import com.kerosenelabs.kindling.exception.KindlingException;
import com.kerosenelabs.kindling.handler.RequestHandler;

public class Main {
    public static void main(String[] args) throws KindlingException {

        KindlingServer server = KindlingServer.getInstance();

        // test request handler
        server.installRequestHandler(new RequestHandler() {
            /**
             * Tell the server what type of request this handler can work with
             */
            @Override
            public boolean accepts(HttpRequest httpRequest) throws KindlingException {
                return httpRequest.getHttpMethod().equals(HttpMethod.GET) && httpRequest.getResource().equals("/");
            }

            /**
             * Do your business logic here
             */
            @Override
            public HttpResponse handle(HttpRequest httpRequest) throws KindlingException {
                return new HttpResponse.Builder()
                        .status(HttpStatus.OK)
                        .contentType(MimeType.APPLICATION_JSON)
                        .content("{\"key\": \"value\"}")
                        .build();
            }

            @Override
            public HttpResponse handleError(Throwable t) throws KindlingException {
                return new HttpResponse.Builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .content(t.getStackTrace().toString())
                        .build();
            }
        });

        // serve our server
        server.serve(8443, Path.of("keystore.p12"), "password");
    }
}
