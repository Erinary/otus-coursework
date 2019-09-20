package ru.otus.erinary.simplewebserver.server;

import lombok.extern.slf4j.Slf4j;
import ru.otus.erinary.simplewebserver.handler.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WebServer {

    private static final int DEFAULT_PORT = 8888;
    private final ServerSocket serverSocket;
    private final Map<String, Handler> handlers;

    public WebServer(int serverPort) throws IOException {
        int port = serverPort > 0 ? serverPort : DEFAULT_PORT;
        this.handlers = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
    }

    public void run() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            SocketListener listener = new SocketListener(socket, handlers);
            listener.start();
        }
    }

    public void addHandler(String path, Handler handler) {
        handlers.put(path, handler);
    }

}
