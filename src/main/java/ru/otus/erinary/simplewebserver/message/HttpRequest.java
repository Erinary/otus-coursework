package ru.otus.erinary.simplewebserver.message;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HttpRequest {

    private HttpMethod method;
    private String path;
    private Map<String, String> pathParameters;
    private String protocolVersion;
    private Map<String, String> headers;
    private byte[] body;

    public enum HttpMethod {
        GET, POST, HEAD, PUT, DELETE, TRACE, OPTIONS, PATCH
    }

}


