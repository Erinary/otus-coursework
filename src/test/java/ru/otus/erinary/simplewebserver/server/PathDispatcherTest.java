package ru.otus.erinary.simplewebserver.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.erinary.simplewebserver.handler.DummyHandler;
import ru.otus.erinary.simplewebserver.handler.Handler;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class PathDispatcherTest {

    private PathDispatcher dispatcher;

    @BeforeEach
    void setup() {
        dispatcher = new PathDispatcher();
    }

    @Test
    void testDispatcher() {
        Handler handler = new DummyHandler("", "");
        Handler anotherHandler = new DummyHandler("", "");
        String path = "/user/{id}/{name}";
        String anotherPath = "/user/{id}";

        dispatcher.addHandler(path, handler);
        dispatcher.addHandler(anotherPath, anotherHandler);
        String realPath = "/user/1/qwerty";
        String anotherRealPath = "/user/1";

        assertSame(handler, dispatcher.getHandler(realPath));
        assertEquals(Map.of("id", "1", "name", "qwerty"), dispatcher.getPathParameters(realPath));

        assertSame(anotherHandler, dispatcher.getHandler(anotherRealPath));
        assertEquals(Map.of("id", "1"), dispatcher.getPathParameters(anotherRealPath));
    }

}