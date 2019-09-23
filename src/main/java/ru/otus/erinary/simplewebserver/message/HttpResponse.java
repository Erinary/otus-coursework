package ru.otus.erinary.simplewebserver.message;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HttpResponse {

    private String protocolVersion;
    private int statusCode;
    private String statusText;
    private Map<String, String> headers;
    private byte[] body;

}
