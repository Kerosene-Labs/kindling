package io.kerosenelabs.kindling;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.kerosenelabs.kindling.constant.MimeType;

/**
 * A generic implementation of {@link RequestHandler}.
 */
public class HttpRequest {
    // stream readers
    private BufferedReader bufferedReader;

    // whole request variables
    private String httpRequest;
    private ArrayList<String> splitHttpRequest;

    // important values
    private String endpoint;
    private HashMap<String, String> headers;
    private HashMap<String, String> cookies;
    private String body;

    public HttpRequest(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public HttpRequest(String httpRequest) {
        this.httpRequest = httpRequest;
        this.splitHttpRequest = getSplitHttpRequest(httpRequest);
    }

    /**
     * Get the HTTP request as an Array List of Strings, where each String is a
     * specific line in the request.
     * 
     * @param httpRequest
     * @return
     */
    private static ArrayList<String> getSplitHttpRequest(String httpRequest) {
        return (ArrayList<String>) Arrays.asList(httpRequest.split("\r\n"));
    }

    /**
     * Get the endpoint for this request. For example, {@code /endpoint}.
     * 
     * @return
     */
    public String getEndpoint() {
        String requestLine = splitHttpRequest.get(0);
        return requestLine.split(" ")[1];
    }

    public HashMap<String, String> getHeaders() {
        ArrayList<String> headerLines = new ArrayList<>();
        for (int n = 0; n < splitHttpRequest.size(); n++) {
            // skip the request line
            if (n == 0) {
                continue;
            }
            // exit at the body separator
            if (splitHttpRequest.get(n).equals("\r\n")) {
                break;
            }
            headerLines.add(splitHttpRequest.get(n));
        }
        return new HashMap<>();
    }

    public String getHeaderValue(String headerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHeaderValue'");
    }

    public HashMap<String, String> getCookies() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCookies'");
    }

    public String getCookieValue(String cookieName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCookieValue'");
    }

    public String getBody() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequestBody'");
    }

    public MimeType getAcceptType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAcceptType'");
    }
}
