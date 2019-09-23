package ru.otus.erinary.simplewebserver.server;

import lombok.extern.slf4j.Slf4j;
import ru.otus.erinary.simplewebserver.handler.Handler;
import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpRequest.HttpMethod;
import ru.otus.erinary.simplewebserver.message.HttpResponse;
import ru.otus.erinary.simplewebserver.message.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SocketListener extends Thread {

    private static final String HTTP_PROTOCOL = "HTTP/1.1";
    private final Socket socket;
    private final Map<String, Handler> handlers;

    public SocketListener(Socket socket, Map<String, Handler> handlers) {
        this.socket = socket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        HttpResponse response = null;
        try {
            HttpRequest request = readRequest();
            log.info("Got a request: {}", request);
            Handler handler = handlers.get(request.getPath());
            switch (request.getMethod()) {
                case GET:
                    response = handler.doGet(request);
                    break;
                case POST:
                    response = handler.doPost(request);
                    break;
                case PUT:
                    response = handler.doPut(request);
                    break;
                case HEAD:
                    response = handler.doHead(request);
                    break;
                case PATCH:
                    response = handler.doPatch(request);
                    break;
                case TRACE:
                    response = handler.doTrace(request);
                    break;
                case DELETE:
                    response = handler.doDelete(request);
                    break;
                case OPTIONS:
                    response = handler.doOptions(request);
                    break;
                default:
                    response = HttpResponse.builder()
                            .protocolVersion(request.getProtocolVersion())
                            .statusCode(HttpStatus.METHOD_NOT_ALLOWED.getCode())
                            .statusText(HttpStatus.METHOD_NOT_ALLOWED.getMessage())
                            .build();
            }
        } catch (Exception e) {
            log.error("Error: {}. Trying to send response with error message", e.getMessage());
            response = HttpResponse.builder()
                    .protocolVersion(HTTP_PROTOCOL)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                    .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                    .body(e.getMessage().getBytes())
                    .build();
        } finally {
            if (response == null) {
                response = HttpResponse.builder()
                        .protocolVersion(HTTP_PROTOCOL)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                        .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                        .build();
            }
            try {
                sendResponse(response);
            } catch (Exception e) {
                log.error("Failed to send response. Error: {}", e.getMessage());
            }
        }
    }

    private HttpRequest readRequest() throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        /*First HTTP line*/
        String[] firstLineData = new String[3];
        String requestLine = readLine(inputStream);
        Map<String, String> headers = new HashMap<>();
        if (requestLine != null) {
            firstLineData = requestLine.split(" ");
        }

        /*Headers*/
        while (true) {
            requestLine = readLine(inputStream);
            if (requestLine == null || requestLine.isEmpty()) {
                break;
            }
            String[] headerLine = requestLine.split(":");
            headers.put(headerLine[0], headerLine[1].trim());
        }

        /*Body*/
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        inputStream.transferTo(byteArrayOutputStream);
        byte[] body = byteArrayOutputStream.toByteArray();

        return HttpRequest.builder()
                .method(HttpMethod.valueOf(firstLineData[0]))
                .path(firstLineData[1])
                .protocolVersion(firstLineData[2])
                .headers(headers)
                .body(body)
                .build();
    }

    private void sendResponse(HttpResponse response) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private String readLine(InputStream inputStream) throws IOException {
        int c;
        StringBuilder builder = new StringBuilder();

        while (true) {
            c = inputStream.read();
            if (c == -1 || c == '\n') {
                break;
            }
            builder.append((char) c);
        }

        if (c == -1 && builder.length() == 0) {
            return null;
        }

        int lastCharPos = builder.length() - 1;
        if (lastCharPos >= 0 && builder.charAt(lastCharPos) == '\r') {
            builder.deleteCharAt(lastCharPos);
        }
        return builder.toString();
    }
}
