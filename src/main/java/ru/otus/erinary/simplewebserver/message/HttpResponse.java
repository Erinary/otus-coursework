package ru.otus.erinary.simplewebserver.message;

import lombok.Data;

import java.util.Map;

@Data
public class HttpResponse {

    private String protocolVersion;
    private int statusCode;
    private String statusText;
    private Map<String, String> headers;
    private String body;

}
