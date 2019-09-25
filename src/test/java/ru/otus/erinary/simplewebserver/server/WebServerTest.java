package ru.otus.erinary.simplewebserver.server;

import com.squareup.okhttp.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.otus.erinary.simplewebserver.handler.DummyHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebServerTest {

    private static final int PORT = 8080;
    private static WebServer webServer;
    private static Thread webServerThread;

    @BeforeAll
    static void setup() throws Exception {
        webServer = new WebServer(PORT);
        webServer.addHandler("/", new DummyHandler());
        webServerThread = new Thread(() -> {
            try {
                webServer.run();
            } catch (Exception ignored) {
            }
        });
        webServerThread.start();
    }

    @AfterAll
    static void cleanup() {
        webServerThread.interrupt();
    }

    @Test
    void testGetHttpRequest() throws Exception {
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
    }

    @Test
    void testPostHttpRequest() throws Exception {
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

}
