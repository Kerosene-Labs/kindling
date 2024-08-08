package io.kerosenelabs.kindling;

public class Main {
    public static void main(String[] args) {
        HttpsServer server = HttpsServer.getInstance();
        server.serve(8443);
    }
}
