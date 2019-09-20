package ru.otus.erinary.simplewebserver.server;

import lombok.extern.slf4j.Slf4j;
import ru.otus.erinary.simplewebserver.handler.Handler;

import java.net.Socket;
import java.util.Map;

@Slf4j
public class SocketListener extends Thread {

    private final Socket socket;
    private final Map<String, Handler> handlers;

    public SocketListener(Socket socket, Map<String, Handler> handlers) {
        this.socket = socket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        super.run();
    }
}
