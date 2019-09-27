package ru.otus.erinary.simplewebserver.handler;

import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpResponse;

public interface Handler {

    default HttpResponse doGet(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doPost(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doHead(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doPut(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doDelete(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doTrace(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doOptions(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    default HttpResponse doPatch(HttpRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
