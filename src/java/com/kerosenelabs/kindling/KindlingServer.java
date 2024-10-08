package com.kerosenelabs.kindling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.kerosenelabs.kindling.constant.HttpStatus;
import com.kerosenelabs.kindling.exception.KindlingException;
import com.kerosenelabs.kindling.handler.RequestHandler;

/**
 * The HTTPS Server singleton
 */
public class KindlingServer {
    private static KindlingServer instance = null;
    private List<RequestHandler> requestHandlers = new ArrayList<>();
    private WorkerThreadNameGenerator workerThreadNameGenerator = new WorkerThreadNameGenerator() {
        @Override
        public String generate() {
            return "req:" + UUID.randomUUID().toString();
        }
    };

    private KindlingServer() {
    }

    public static KindlingServer getInstance() {
        if (instance == null) {
            instance = new KindlingServer();
        }
        return instance;
    }

    /**
     * Set the worker thread name generator. By default, this defaults to a
     * anonymous class that generates a random UUID.
     * 
     * @param workerThreadNameGeneratorClass
     * @throws KindlingException
     */
    public void setWorkerThreadNameGenerator(Class<WorkerThreadNameGenerator> workerThreadNameGeneratorClass)
            throws KindlingException {
        try {
            workerThreadNameGenerator = workerThreadNameGeneratorClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new KindlingException(e);
        }
    }

    /**
     * Install a {@link RequestHandler}.
     * 
     * @param requestHandler
     * @throws KindlingException
     */
    public void installRequestHandler(RequestHandler requestHandler) throws KindlingException {
        for (RequestHandler handler : requestHandlers) {
            if (handler.equals(requestHandler)) {
                throw new KindlingException("Programming error, duplicate request handler");
            }
        }
        requestHandlers.add(requestHandler);
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
        String workerThreadName = workerThreadNameGenerator.generate();
        Thread.ofVirtual().name(workerThreadName).start(() -> {
            try (
                    InputStream inputStream = sslSocket.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    OutputStream outputStream = sslSocket.getOutputStream();) {

                // parse our http request
                HttpRequest httpRequest = new HttpRequest(bufferedReader);

                // iterate over request handlers, finding one that can take this request
                HttpResponse response = null;
                for (RequestHandler requestHandler : requestHandlers) {
                    if (requestHandler.accepts(httpRequest)) {
                        try {
                            response = requestHandler.handle(httpRequest);
                        } catch (Exception e) {
                            response = requestHandler.handleError(e);
                        }
                    }
                }

                // if there were no request handlers
                if (response == null) {
                    response = new HttpResponse.Builder().status(HttpStatus.NOT_FOUND).content("Not Found").build();
                }

                // write our response
                outputStream.write(response.toString().getBytes());

            } catch (IOException | KindlingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
