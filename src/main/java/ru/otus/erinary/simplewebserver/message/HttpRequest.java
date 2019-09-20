package ru.otus.erinary.simplewebserver.message;

import lombok.Data;

import java.util.Map;

@Data
public class HttpRequest {

    private HttpMethod method;
    private String requestTarget;
    private String protocolVersion;
    private Map<String, String> headers;
    private String body;

    private enum HttpMethod {
        GET, POST, HEAD, PUT, DELETE, TRACE, OPTIONS, PATCH
    }

}


