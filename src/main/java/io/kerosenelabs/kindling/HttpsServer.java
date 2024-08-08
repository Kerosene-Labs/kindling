package io.kerosenelabs.kindling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import io.kerosenelabs.kindling.exception.KindlingException;
import io.kerosenelabs.kindling.handler.RequestHandler;

/**
 * The HTTPS Server singleton
 */
public class HttpsServer {
    private static HttpsServer instance = null;
    private List<RequestHandler> requestHandlers = new ArrayList<>();

    private HttpsServer() {
    }

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
     * @throws KindlingException
     */
    public void serve(int port) throws KindlingException {
        Thread thread = Thread.ofVirtual().name("k-https").start(() -> {
            SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            try (SSLServerSocket socket = (SSLServerSocket) socketFactory.createServerSocket(port)) {

            }
        });

        // set the thread exception handler
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

            }
        });
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new KindlingException(e);
        }
    }
}
