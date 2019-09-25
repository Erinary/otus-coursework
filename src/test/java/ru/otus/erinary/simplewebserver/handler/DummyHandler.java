package ru.otus.erinary.simplewebserver.handler;

import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpResponse;
import ru.otus.erinary.simplewebserver.message.HttpStatus;

public class DummyHandler implements Handler {

    @Override
    public HttpResponse doGet(HttpRequest request) {
        HttpResponse response;
        try {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.OK.getCode())
                    .statusText(HttpStatus.OK.getMessage())
                    .body("GET: Got a message!".getBytes())
                    .build();
        } catch (Exception e) {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                    .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                    .body("GET: Something went wrong!".getBytes())
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
                    .body("POST: Got a message!".getBytes())
                    .build();
        } catch (Exception e) {
            response = HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                    .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                    .body("POST: Something went wrong!".getBytes())
                    .build();
        }
        return response;
    }

}
