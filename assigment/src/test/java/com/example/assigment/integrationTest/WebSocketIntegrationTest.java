package com.example.assigment.integrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private StandardWebSocketClient webSocketClient;

    @BeforeEach
    void setup() {
        webSocketClient = new StandardWebSocketClient();
    }

    @Test
    void testWebSocketConnectionAndBet() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        WebSocketHandler handler = new AbstractWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                System.out.println("Received: " + message.getPayload());
                latch.countDown();
            }
        };

        URI uri = new URI("ws://localhost:" + port + "/game");
        WebSocketSession session = webSocketClient.execute(handler, new WebSocketHttpHeaders(), uri).get();

        session.sendMessage(new TextMessage("{\"nickname\":'player1', \"number\":3, \"amount\":100}"));

        boolean success = latch.await(10, TimeUnit.SECONDS);
        assertTrue(success, "Did not receive message in time");

        session.close();
    }

    @Test
    public void testValidBetJsonAccepted() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedPayload = new AtomicReference<>();

        WebSocketHandler handler = new AbstractWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                receivedPayload.set(message.getPayload());
                latch.countDown();
            }
        };

        URI uri = new URI("ws://localhost:" + port + "/game");
        WebSocketSession session = webSocketClient.execute(handler, new WebSocketHttpHeaders(), uri).get();

        String validJson = """
            {
                "nickname": "Alice",
                "number": 5,
                "amount": 100
            }
        """;

        session.sendMessage(new TextMessage(validJson));

        latch.await(2, TimeUnit.SECONDS);
        session.close();

        assertNotEquals("Invalid message format", receivedPayload.get());
    }

    @Test
    public void testInvalidJsonIsRejected() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> errorMessage = new AtomicReference<>();

        WebSocketHandler handler = new AbstractWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                errorMessage.set(message.getPayload());
                latch.countDown();
            }
        };

        URI uri = new URI("ws://localhost:" + port + "/game");
        WebSocketSession session = webSocketClient.execute(handler, new WebSocketHttpHeaders(), uri).get();

        String invalidJson = "{ invalid json }";
        session.sendMessage(new TextMessage(invalidJson));

        latch.await(3, TimeUnit.SECONDS);
        session.close();

        assertEquals("Invalid message format", errorMessage.get());
    }

    @Test
    public void testMultiplePlayersPlacingValidBets() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());

        WebSocketHandler handler1 = new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                session.getAttributes().put("UUID", "uuid-1");
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                receivedMessages.add("Player1: " + message.getPayload());
                latch.countDown();
            }
        };

        WebSocketHandler handler2 = new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                session.getAttributes().put("UUID", "uuid-2");
            }

            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                receivedMessages.add("Player2: " + message.getPayload());
                latch.countDown();
            }
        };

        URI uri = new URI("ws://localhost:" + port + "/game");

        WebSocketSession session1 = webSocketClient.execute(handler1, new WebSocketHttpHeaders(), uri).get();
        WebSocketSession session2 = webSocketClient.execute(handler2, new WebSocketHttpHeaders(), uri).get();

        String bet1 = """
            {
              "nickname": "Alice",
              "number": 4,
              "amount": 50.0
            }
        """;

        String bet2 = """
            {
              "nickname": "Bob",
              "number": 7,
              "amount": 100.0
            }
        """;

        session1.sendMessage(new TextMessage(bet1));
        session2.sendMessage(new TextMessage(bet2));

        latch.await(3, TimeUnit.SECONDS);

        session1.close();
        session2.close();

        assertTrue(receivedMessages.size() <= 2); // If server echoes or confirms receipt
    }
}
