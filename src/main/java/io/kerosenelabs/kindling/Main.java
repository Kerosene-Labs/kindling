package io.kerosenelabs.kindling;

import java.nio.file.Path;
import java.util.HashMap;

import io.kerosenelabs.kindling.constant.HttpMethod;
import io.kerosenelabs.kindling.constant.HttpStatus;
import io.kerosenelabs.kindling.exception.KindlingException;
import io.kerosenelabs.kindling.handler.RequestHandler;

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
                        .headers(new HashMap<>() {
                            {
                                put("Content-Type", "application/json");
                            }
                        })
                        .content("{\"key\": \"value\"}")
                        .build();
            }
        });

        // serve our server
        server.serve(8443, Path.of("mykeystore.p12"), "password");
    }
}
