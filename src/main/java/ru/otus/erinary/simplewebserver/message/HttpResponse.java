package ru.otus.erinary.simplewebserver.message;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class HttpResponse {

    private String protocolVersion;
    private int statusCode;
    private String statusText;

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private Map<String, String> headers = new HashMap<>();

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private byte[] body = {};

}
