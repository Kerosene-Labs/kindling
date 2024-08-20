# Kindling

*The fuel that'll ignite your application.*

A programmable TLS HTTP/1.1 server written with no dependencies.

## Key Features
* SSL/TLS only (using `SSLServerSocket`)
* No magic with visible control flow
* Light weight
* No Dependencies
* Virtual Threads

## Example

Below is an example implementation of a Kindling Server with a simple request handler that responds with a JSON object
to a `GET` request on the `/` resource.

```java
public class Main {
    public static void main(String[] args) throws KindlingException {
        KindlingServer server = KindlingServer.getInstance();
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
```

Let's break down the components above.

* KindlingServer
  * The singleton that contains the application context
  * Manages the `SSLServerSocket` and the virtual thread that is assigned to each request
* RequestHandler
  * Accepts
    * Called when a request is received
    * Return a boolean telling the server if we can handle this request
  * Handle
    * If the `accepts()` method returns true, `handle()` is called
    * Do your business logic here, returning an `HttpResponse`
* Server Serve
  * Start serving the `SSLServerSocket` on the provided port with the given `P12` keystore