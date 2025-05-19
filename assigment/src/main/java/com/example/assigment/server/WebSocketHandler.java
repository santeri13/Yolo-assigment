package com.example.assigment.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final GameService gameService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uuid = UUID.randomUUID().toString();
        session.getAttributes().put("UUID", uuid);
        gameService.addSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Bet bet = objectMapper.readValue(message.getPayload(), Bet.class);
            bet.setUUID((String) session.getAttributes().get("UUID"));
            gameService.placeBet(session, bet);
        } catch (IOException e) {
            session.sendMessage(new TextMessage("Invalid message format"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        gameService.removeSession(session);
    }
}
