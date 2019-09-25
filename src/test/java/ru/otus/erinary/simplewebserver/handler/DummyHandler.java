package ru.otus.erinary.simplewebserver.handler;

import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpResponse;
import ru.otus.erinary.simplewebserver.message.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class DummyHandler implements Handler {

    private final Map<String, String> methodMessages;
    private static final String GET_KEY = "get";
    private static final String POST_KEY = "post";
    private static final String ERROR_MESSAGE = "Something went wrong!";

    public DummyHandler(String getMessage, String postMessage) {
        this.methodMessages = new HashMap<>();
        methodMessages.put(GET_KEY, getMessage);
        methodMessages.put(POST_KEY, postMessage);
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        HttpResponse response;
        try {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.OK.getCode())
                    .statusText(HttpStatus.OK.getMessage())
                    .body(methodMessages.get(GET_KEY).getBytes())
                    .build();
        } catch (Exception e) {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                    .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                    .body(ERROR_MESSAGE.getBytes())
                    .build();
        }
        return response;
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        HttpResponse response;
        try {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.OK.getCode())
                    .statusText(HttpStatus.OK.getMessage())
                    .body(methodMessages.get(POST_KEY).getBytes())
                    .build();
        } catch (Exception e) {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                    .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                    .body(ERROR_MESSAGE.getBytes())
                    .build();
        }
        return response;
    }

}
