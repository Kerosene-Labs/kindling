package com.kerosenelabs.kindling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.kerosenelabs.kindling.constant.HttpMethod;
import com.kerosenelabs.kindling.dto.HttpRequestHead;
import com.kerosenelabs.kindling.exception.KindlingException;
import com.kerosenelabs.kindling.handler.RequestHandler;

/**
 * A generic implementation of {@link RequestHandler}.
 */
public class HttpRequest {
    private HttpMethod httpMethod;
    private String resource;
    private String path;
    private HashMap<String, String> queryParameters;
    private String protocolVersion;
    private HashMap<String, String> headers;
    private String content;
    private String[] x;

    public HttpRequest(BufferedReader bufferedReader) throws KindlingException {
        List<String> messageHead;
        try {
            messageHead = readRawMessageHead(bufferedReader);
        } catch (IOException e) {
            throw new KindlingException(e);
        }

        // do http request line
        HttpRequestHead httpRequestLine = parseRequestLine(messageHead);
        httpMethod = httpRequestLine.getHttpMethod();
        resource = httpRequestLine.getResource();
        protocolVersion = httpRequestLine.getProtocol();
        path = resource.split("\\?")[0];
        queryParameters = parseQueryParameters(resource);

        // do headers
        headers = parseHttpHeaders(messageHead);

        // do the content (if there's no content-length, we assume there's no body)
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        if (contentLength > 0) {
            try {
                content = parseContent(contentLength, bufferedReader);
            } catch (IOException e) {
                throw new KindlingException(e);
            }
        }
    }

    /**
     * Get the Resource (path and query parameters)
     * 
     * @return
     */
    public String getResource() {
        return resource;
    }

    /**
     * Get the path
     * 
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the query parameters
     * 
     * @return
     */
    public HashMap<String, String> getQueryParmeters() {
        return queryParameters;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getContent() {
        return content;
    }

    /**
     * Get a valid re-creation of the HTTP message
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder()
                .append(String.format("%s %s %s\r\n", httpMethod.name(), resource, protocolVersion));
        for (Entry<String, String> entrySet : headers.entrySet()) {
            stringBuilder.append(String.format("%s: %s\r\n", entrySet.getKey(), entrySet.getValue()));
        }
        stringBuilder.append("\r\n");
        if (content != null) {
            stringBuilder.append(content);
        }
        return stringBuilder.toString();
    }

    /**
     * Read the raw HTTP Request message head as a list of Strings. The "HTTP
     * Request Message
     * Head" is known colloquially in this project as the literal text before the
     * content separator.
     * 
     * @param bufferedReader Buffered reader from the socket
     * @return Raw HTTP request message head as a list of Strings
     * @throws IOException
     */
    private static List<String> readRawMessageHead(BufferedReader bufferedReader) throws IOException {
        List<String> messageHead = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).equals("\r\n\r\n")) {
            messageHead.add(line);
            if (line.isEmpty()) {
                break;
            }
        }
        return messageHead;
    }

    /**
     * Parse the request line. An HTTP request line is always the first line in an
     * HTTP request message.
     * 
     * @param messageHead
     * @return HttpRequestLine object
     */
    private static HttpRequestHead parseRequestLine(List<String> messageHead) {
        String requestLine = messageHead.get(0);
        String[] splitRequestLine = requestLine.split(" ");
        return new HttpRequestHead(HttpMethod.valueOf(splitRequestLine[0]), splitRequestLine[1], splitRequestLine[2]);
    }

    /**
     * Parses the query parameters out of the given resource as a map.
     * 
     * @param resource Resource string (ex: /registry?q=lombok)
     * @return
     * @throws KindlingException
     */
    private static HashMap<String, String> parseQueryParameters(String resource) throws KindlingException {
        HashMap<String, String> params = new HashMap<>();

        // split our resource, ensure we were given query params
        String[] splitResource = resource.split("\\?");
        if (splitResource.length == 1) {
            return params;
        } else if (splitResource.length > 2) {
            throw new KindlingException("Malformed query parameters, multiple '?' separators");
        }

        // split again, this time by param separators
        String[] separatedParams = splitResource[1].split("\\&");

        // iterate over the split params, this time splitting by key/value pair and
        // decoding the keys and values
        for (String keyValuePair : separatedParams) {
            String[] splitKeyValuePair = keyValuePair.split("\\=");
            try {
                params.put(URLDecoder.decode(splitKeyValuePair[0], "UTF8"),
                        URLDecoder.decode(splitKeyValuePair[1], "UTF8"));
            } catch (UnsupportedEncodingException e) {
                throw new KindlingException(e);
            }
        }
        System.out.println("params: " + params.toString());
        return params;
    }

    /**
     * Parse HTTP headers from the message head
     * 
     * @param messageHead
     * @return List of ImmutableHttpHeader(s)
     */
    private static HashMap<String, String> parseHttpHeaders(List<String> messageHead) {
        HashMap<String, String> headers = new HashMap<>();

        // note: n=1 here because we are only parsing headers
        int n = 1;
        for (n = 1; n < messageHead.size(); n++) {
            String currentLine = messageHead.get(n);

            // determine our key/value pair for this header string
            StringBuilder headerName = new StringBuilder();
            StringBuilder headerValue = new StringBuilder();

            // iterate over each character in the string
            char[] currentLineChars = currentLine.toCharArray();
            boolean passedSeparator = false;
            int headerCharN;
            for (headerCharN = 0; headerCharN < currentLineChars.length; headerCharN++) {
                // if this is the first colon
                if (currentLineChars[headerCharN] == ':' && !passedSeparator) {
                    passedSeparator = true;
                    headerCharN += 1;
                    continue;
                }

                // if we're not past the first colon, this must be the header name
                if (!passedSeparator) {
                    headerName.append(currentLineChars[headerCharN]);
                } else {
                    headerValue.append(currentLineChars[headerCharN]);
                }
            }

            if (headerName.isEmpty()) {
                continue;
            }

            headers.put(headerName.toString(), headerValue.toString());
        }
        return headers;
    }

    /**
     * Parse the HTTP Content (after the content separator).
     * 
     * @param contentLength  Int value of the Content-Length header
     * @param bufferedReader Buffered Reader from the socket
     * @return String containing the content
     * @throws IOException
     */
    private static String parseContent(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] content = new char[contentLength];
        bufferedReader.read(content, 0, contentLength);
        return new String(content);
    }
}
