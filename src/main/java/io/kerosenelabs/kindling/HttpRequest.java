package io.kerosenelabs.kindling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A parser and data transfer object for a raw HTTP request.
 */
public class HttpRequest {
    // whole request variables
    private final String httpRequest;
    private final List<String> splitHttpRequest;

    // important values
    private String resource;
    private HashMap<String, String> headers;
    private HashMap<String, String> cookies;
    private String body;

    public HttpRequest(String httpRequest) {
        this.httpRequest = httpRequest;
        splitHttpRequest = getSplitHttpRequest(httpRequest);
        resource = parseResource();
        headers = parseHeaders();

    }

    /**
     * Get the HTTP request as an Array List of Strings, where each String is a
     * specific line in the request.
     * 
     * @param httpRequest
     * @return
     */
    private static List<String> getSplitHttpRequest(String httpRequest) {
        return Arrays.asList(httpRequest.split("\r\n"));
    }

    /**
     * Parse ther esource for this request. For example, {@code /resource}.
     * 
     * @return
     */
    private String parseResource() {
        String requestLine = splitHttpRequest.get(0);
        return requestLine.split(" ")[1];
    }

    /**
     * Par
     * * @retur
     */
    private HashMap<String, String> parseHeaders() {
        // separate out the request line and body separator
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

        // iterate over header lines, separate by :
        HashMap<String, String> headers = new HashMap<>();
        // for (n = 1; n < headerLines.)
        return headers;
    }
}
