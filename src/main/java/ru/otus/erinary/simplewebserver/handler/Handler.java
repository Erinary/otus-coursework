package ru.otus.erinary.simplewebserver.handler;

import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpResponse;

public interface Handler {

    default void doGet(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doHead(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doPut(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doDelete(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doTrace(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doOptions(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default void doPatch(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
