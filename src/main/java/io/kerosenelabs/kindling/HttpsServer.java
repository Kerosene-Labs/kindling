package io.kerosenelabs.kindling;

import java.util.ArrayList;
import java.util.HashMap;

import io.kerosenelabs.kindling.exception.KindlingException;

/**
 * The HTTPS Server singleton
 */
public class HttpsServer {
    private int port = 443;
    private static HttpsServer instance = null;

    public static HttpsServer getInstance() {
        if (instance == null) {
            instance = new HttpsServer();
        }
        return instance;
    }

    /**
     * Add a request handler to this server
     * 
     * @param requestHandler
     * @throws KindlingException
     */
    // public void addRequestHandler(String name, RequestHandler requestHandler)
    // throws KindlingException {
    // if (requestHandlers.get(name) != null) {
    // throw new KindlingException("A request handler with the given name already
    // exists for this server");
    // }
    // this.requestHandlers.put(name, requestHandler);
    // }

    /**
     * Start serving the application
     * 
     * @return
     */
    public int serve() {
        return 0;
    }
}
