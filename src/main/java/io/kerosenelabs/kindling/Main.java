package io.kerosenelabs.kindling;

import java.nio.file.Path;

import io.kerosenelabs.kindling.constant.HttpStatus;
import io.kerosenelabs.kindling.exception.KindlingException;
import io.kerosenelabs.kindling.handler.RequestHandler;

public class Main {
    public static void main(String[] args) throws KindlingException {

        KindlingServer server = KindlingServer.getInstance();

        // test request handler
        server.installRequestHandler(new RequestHandler() {
            @Override
            public boolean acceptResource(String resource) throws KindlingException {
                return true;
            }

            @Override
            public HttpResponse handle(HttpRequest httpRequest) throws KindlingException {
                return new HttpResponse.Builder()
                        .status(HttpStatus.OK)
                        .content("<h1>Hello from Kindling!</h1>")
                        .build();
            }
        });

        // serve our server
        server.serve(8443, Path.of("mykeystore.p12"), "password");
    }
}
