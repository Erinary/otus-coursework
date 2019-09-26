package ru.otus.erinary.simplewebserver.server;

import com.squareup.okhttp.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.erinary.simplewebserver.handler.DummyHandler;
import ru.otus.erinary.simplewebserver.message.HttpRequest;
import ru.otus.erinary.simplewebserver.message.HttpResponse;
import ru.otus.erinary.simplewebserver.message.HttpStatus;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebServerTest {

    private static final int PORT = 8080;
    private WebServer webServer;
    private Thread webServerThread;

    @BeforeEach
    void setup() throws Exception {
        webServer = new WebServer(PORT);
        webServerThread = new Thread(() -> {
            try {
                webServer.run();
            } catch (Exception ignored) {
            }
        });
    }

    @AfterEach
    void cleanup() throws IOException {
        webServer.close();
        webServerThread.interrupt();
    }

    @Test
    void testGetHttpRequest() throws Exception {
        webServer.addHandler("/", new DummyHandler(
                "GET: Got a message!",
                "POST: Got a message!"
        ));
        webServer.addHandler("/another", new DummyHandler(
                "Another handler - GET: Got a message!",
                "Another handler - POST: Got a message!"
        ));
        webServerThread.start();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
        String responseBody = response.body().string();
        assertTrue(responseBody.contains("GET: Got a message!"));

        Request anotherRequest = new Request.Builder()
                .url("http://localhost:8080/another")
                .get()
                .build();
        Response anotherResponse = client.newCall(anotherRequest).execute();
        assertEquals(200, anotherResponse.code());
        assertEquals("OK", anotherResponse.message());
        String anotherResponseBody = anotherResponse.body().string();
        assertTrue(anotherResponseBody.contains("Another handler - GET: Got a message!"));
    }

    @Test
    void testPostHttpRequest() throws Exception {
        webServer.addHandler("/", new DummyHandler(
                "GET: Got a message!",
                "POST: Got a message!"
        ));
        webServerThread.start();

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "Hello server!");
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
        String responseBody = response.body().string();
        assertTrue(responseBody.contains("POST: Got a message!"));
    }

    @Test
    void testRequestWithQueryParameters() throws Exception {
        webServer.addHandler("/query", new DummyHandler(null, null) {
            @Override
            public HttpResponse doGet(HttpRequest request) {
                HttpResponse response;
                try {
                    response = HttpResponse.builder()
                            .protocolVersion(request.getProtocolVersion())
                            .statusCode(HttpStatus.OK.getCode())
                            .statusText(HttpStatus.OK.getMessage())
                            .body(request.getQueryParameters().toString().getBytes())
                            .build();
                } catch (Exception e) {
                    response = HttpResponse.builder()
                            .protocolVersion(request.getProtocolVersion())
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.getCode())
                            .statusText(HttpStatus.INTERNAL_SERVER_ERROR.getMessage())
                            .build();
                }
                return response;
            }
        });
        webServerThread.start();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://localhost:8080/query?p1&p2&p3=one+two&p4=three%21")
                .get()
                .build();
        Response response = client.newCall(request).execute();
        assertEquals(200, response.code());
        assertEquals("OK", response.message());
        String responseBody = response.body().string();
        System.out.println(responseBody);
        assertTrue(responseBody.contains("{p1=null, p2=null, p3=one+two, p4=three%21}"));
    }

}
