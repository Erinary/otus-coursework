package ru.otus.erinary.simplewebserver.server;

import lombok.extern.slf4j.Slf4j;
import ru.otus.erinary.simplewebserver.handler.Handler;
import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpRequest.HttpMethod;
import ru.otus.erinary.simplewebserver.message.HttpResponse;
import ru.otus.erinary.simplewebserver.message.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class SocketListener extends Thread {

    private static final String HTTP_PROTOCOL = "HTTP/1.1";
    private final Socket socket;
    private final PathDispatcher dispatcher;

    SocketListener(Socket socket, PathDispatcher dispatcher) {
        this.socket = socket;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        HttpResponse response = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            HttpRequest request = readRequest(inputStream);
            if (request == null) {
                response = HttpResponse.builder()
                        .protocolVersion(HTTP_PROTOCOL)
                        .statusCode(HttpStatus.BAD_REQUEST.getCode())
                        .statusText(HttpStatus.BAD_REQUEST.getMessage())
                        .body("Received empty request".getBytes())
                        .build();
                return;
            }
            if (!HTTP_PROTOCOL.equals(request.getProtocolVersion())) {
                response = HttpResponse.builder()
                        .protocolVersion(HTTP_PROTOCOL)
                        .statusCode(HttpStatus.BAD_REQUEST.getCode())
                        .statusText(HttpStatus.BAD_REQUEST.getMessage())
                        .body("Unsupported HTTP protocol version".getBytes())
                        .build();
                return;
            }
            log.info("Got a request: {}", request);
            response = dispatchRequest(request);
        } catch (Exception e) {
            log.error("Error while handling request:", e);
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
                if (outputStream != null) {
                    sendResponse(response, outputStream);
                    log.info("Response was sent");
                }
            } catch (Exception e) {
                log.error("Failed to send response", e);
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private HttpRequest readRequest(InputStream inputStream) throws IOException {
        log.info("Reading request");
        /*First HTTP line*/
        String requestLine = readLine(inputStream);
        log.info("First line read: {}", requestLine);
        if (requestLine == null) {
            log.info("Empty first line of request");
            return null;
        }
        String[] firstLineData = requestLine.split(" ");

        /*Query parameters*/
        String path;
        int queryStartIndex = firstLineData[1].indexOf("?");
        Map<String, List<String>> queryParameters = new HashMap<>();
        if (queryStartIndex == -1) {
            path = firstLineData[1];
        } else {
            path = firstLineData[1].substring(0, queryStartIndex);
            String[] queryParts = firstLineData[1].substring(queryStartIndex + 1).split("&");
            for (String part : queryParts) {
                String[] query = part.split("=");
                String queryKey = query[0];
                List<String> queryValues;

                if (queryParameters.get(queryKey) == null) {
                     queryValues = new ArrayList<>();
                     queryParameters.put(queryKey, queryValues);
                } else {
                    queryValues = queryParameters.get(queryKey);
                }

                if (query.length > 1) {
                    queryValues.add(URLDecoder.decode(query[1], StandardCharsets.UTF_8));
                } else {
                    queryValues.add(null);
                }
            }
        }

        /*Headers*/
        Map<String, String> headers = new HashMap<>();
        while (true) {
            requestLine = readLine(inputStream);
            log.info("Line read: {}", requestLine);
            if (requestLine == null || requestLine.isEmpty()) {
                break;
            }
            String[] headerLine = requestLine.split(":");
            headers.put(headerLine[0], headerLine[1].trim());
        }

        /*Body*/
        String contentLengthValue = headers.get("Content-Length");
        byte[] body;
        if (contentLengthValue == null) {
            body = new byte[0];
        } else {
            int contentLen = Integer.parseInt(contentLengthValue);
            body = inputStream.readNBytes(contentLen);
        }

        return HttpRequest.builder()
                .method(HttpMethod.valueOf(firstLineData[0]))
                .path(path)
                .protocolVersion(firstLineData[2])
                .headers(headers)
                .queryParameters(queryParameters)
                .body(body)
                .build();
    }

    private HttpResponse dispatchRequest(HttpRequest request) {
        Handler handler = dispatcher.getHandler(request.getPath());
        if (handler == null) {
            return HttpResponse.builder()
                    .protocolVersion(request.getProtocolVersion())
                    .statusCode(HttpStatus.NOT_FOUND.getCode())
                    .statusText(HttpStatus.NOT_FOUND.getMessage())
                    .build();
        }
        request.setPathParameters(dispatcher.getPathParameters(request.getPath()));
        switch (request.getMethod()) {
            case GET:
                return handler.doGet(request);
            case POST:
                return handler.doPost(request);
            case PUT:
                return handler.doPut(request);
            case HEAD:
                return handler.doHead(request);
            case PATCH:
                return handler.doPatch(request);
            case TRACE:
                return handler.doTrace(request);
            case DELETE:
                return handler.doDelete(request);
            case OPTIONS:
                return handler.doOptions(request);
            default:
                return HttpResponse.builder()
                        .protocolVersion(request.getProtocolVersion())
                        .statusCode(HttpStatus.METHOD_NOT_ALLOWED.getCode())
                        .statusText(HttpStatus.METHOD_NOT_ALLOWED.getMessage())
                        .build();
        }
    }

    private void sendResponse(HttpResponse response, OutputStream outputStream) throws IOException {
        response.getHeaders().put("Content-Length", String.valueOf(response.getBody().length));
        String firstLineData = new StringJoiner(" ", "", System.lineSeparator())
                .add(response.getProtocolVersion())
                .add(Integer.toString(response.getStatusCode()))
                .add(response.getStatusText())
                .toString();
        String headers = String.join(System.lineSeparator(),
                response.getHeaders().entrySet().stream().map(entry ->
                        new StringJoiner(": ")
                                .add(entry.getKey())
                                .add(entry.getValue())
                                .toString()).toArray(String[]::new));
        String serviceData = new StringJoiner("\r\n")
                .add(firstLineData)
                .add(headers)
                .add("")
                .toString();
        log.info("Service data for response: {}", serviceData);
        outputStream.write(serviceData.getBytes());
        outputStream.write(response.getBody());
        outputStream.flush();
        outputStream.close();
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
