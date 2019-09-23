package ru.otus.erinary.simplewebserver.handler;

import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpResponse;

public class SimpleHandler implements Handler {

    @Override
    public HttpResponse doGet(HttpRequest request) {
        return null;
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        return null;
    }
}
