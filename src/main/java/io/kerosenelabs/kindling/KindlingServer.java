package io.kerosenelabs.kindling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import io.kerosenelabs.kindling.exception.KindlingException;
import io.kerosenelabs.kindling.handler.RequestHandler;

/**
 * The HTTPS Server singleton
 */
public class KindlingServer {
    private static KindlingServer instance = null;
    private HashMap<String, RequestHandler> requestHandlers = new HashMap<>();

    private KindlingServer() {
    }

    public static KindlingServer getInstance() {
        if (instance == null) {
            instance = new KindlingServer();
        }
        return instance;
    }

    /**
     * Install a {@link RequestHandler}.
     * 
     * @param requestHandler
     * @throws KindlingException
     */
    public void installRequestHandler(String resourceMapping, RequestHandler requestHandler) throws KindlingException {
        for (String mapping : requestHandlers.keySet()) {
            if (mapping.equals(resourceMapping)) {
                throw new KindlingException("Programming error, duplicate resource mapping");
            }
        }
        requestHandlers.put(resourceMapping, requestHandler);
    }

    /**
     * Start serving the application
     * 
     * @return
     * @throws KindlingException
     */
    public void serve(int port, Path keystorePath, String keyStorePassword) throws KindlingException {
        // set the system preferences for the keystore path and password
        System.setProperty("javax.net.ssl.keyStore", keystorePath.toAbsolutePath().toString());
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);

        // create an atomic reference to store any critical exceptions that occur within
        // the thread
        AtomicReference<Throwable> threadException = new AtomicReference<>();

        Thread thread = Thread.ofVirtual().name("k-https").start(() -> {
            SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            try (SSLServerSocket socket = (SSLServerSocket) socketFactory.createServerSocket(port)) {
                while (true) {
                    SSLSocket sslSocket = (SSLSocket) socket.accept();
                    dispatchWorker(sslSocket);
                }
            } catch (Exception e) {
                threadException.set(e);
            }
        });

        // set the thread exception handler
        thread.setUncaughtExceptionHandler((t, throwable) -> threadException.set(throwable));

        // wait for the main thread to terminate
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new KindlingException(e);
        }

        if (threadException.get() != null) {
            throw new KindlingException(threadException.get());
        }
    }

    /**
     * Dispatch a worker to handle a particular request
     * 
     * @param sslSocket
     */
    private void dispatchWorker(SSLSocket sslSocket) {
        Thread.startVirtualThread(() -> {
            try (
                    InputStream inputStream = sslSocket.getInputStream();
                    InputStreamReader inputStreamReder = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReder);
                    OutputStream OutputStream = sslSocket.getOutputStream();) {

                // parse our http request
                HttpRequest httpRequest = new HttpRequest(bufferedReader);
                System.out.println(httpRequest.toString());
            } catch (IOException | KindlingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
