package io.kerosenelabs.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.kerosenelabs.kindling.handler.HttpRequest;

public class HttpRequestTest {
    private String rawGetRequest;

    @BeforeEach
    public void setGetRequest() {
        rawGetRequest = new StringBuilder("GET /resource HTTP/1.1\r\n")
                .append("Host: www.example.com\r\n")
                .append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\r\n")
                .append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n")
                .append("Accept-Language: en-US,en;q=0.5\r\n")
                .append("Accept-Encoding: gzip, deflate, br\r\n")
                .append("Connection: keep-alive\r\n")
                .append("Upgrade-Insecure-Requests: 1\r\n")
                .append("\r\n")
                .toString();
    }

    @Test
    public void shouldParseHttpRequest_WhenGivenValidGetRequest() {
        HttpRequest httpRequest = new HttpRequest(rawGetRequest);
    }
}
