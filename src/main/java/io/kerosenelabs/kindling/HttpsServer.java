package io.kerosenelabs.kindling;

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
     * Start serving the application
     * 
     * @return
     */
    public void serve() {
        
    }
}
