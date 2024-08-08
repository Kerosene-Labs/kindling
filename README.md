# Kindling

*The fuel that'll ignite your application.*

A programmable TLS HTTP/1.1 server library written in pure Java with no dependencies.

## Usage

```java
public static void startServer() throws KindlingException {
    // get our KindlingServer singleton
    KindlingServer server = KindlingServer.getInstance();

    // add a request handler
    server.installRequestHandler(new RequestHandler() {
        /**
         * Tell the server what type of request this handler can work with
         */
        @Override
        public boolean accepts(HttpMethod httpMethod, String resource) throws KindlingException {
            return httpMethod.equals(HttpMethod.GET) && resource.equals("/");
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
                            put("Content-Type", "text/html");
                        }
                    })
                    .content("<h1>Hello from Kindling!</h1>")
                    .build();
        }
    });

    // serve our server
    server.serve(8443, Path.of("mykeystore.p12"), "password");
}
```
