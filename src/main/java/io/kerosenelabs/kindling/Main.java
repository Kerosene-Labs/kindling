package io.kerosenelabs.kindling;

import java.net.http.HttpRequest;

import io.kerosenelabs.kindling.constant.HttpStatus;
import io.kerosenelabs.kindling.exception.KindlingException;
import io.kerosenelabs.kindling.handler.RequestHandler;

public class Main {
    public static void main(String[] args) throws KindlingException {

        KindlingServer server = KindlingServer.getInstance();

        // test request handler
        server.installRequestHandler("/", new RequestHandler() {
            @Override
            public HttpResponse handle(HttpRequest httpRequest) throws KindlingException {
                return new HttpResponse.Builder().status(HttpStatus.OK).build();
            }
        });

        // serve our server
        server.serve(8443);
    }
}
