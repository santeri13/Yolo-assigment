package com.example.assigment.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, Bet> bets = new ConcurrentHashMap<>();
    private final List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());

    public void placeBet(WebSocketSession session, Bet bet) throws IOException {
        if (!Validator.isValidBet(bet)) {
            try {
                session.sendMessage(new TextMessage("Invalid bet"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        bets.put(bet.getUUID(), bet);
    }

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 10000)
    public void runGameRound() throws IOException {
        int winningNumber = new Random().nextInt(10) + 1;
        List<WinResult> winners = new ArrayList<>();

        for (Bet bet : bets.values()) {
            if (bet.getNumber() == winningNumber) {
                double winnings = bet.getAmount() * 9.9;
                winners.add(new WinResult(bet.getNickname(), winnings, bet.getUUID()));
            } else {
                sendToPlayer(bet.getUUID(), "You lost. Winning number was: " + winningNumber);
            }
        }

        for (WinResult winner : winners) {
            sendToPlayer(winner.getUUID(), "You won: " + winner.getWin());
            winner.setUUID(null);
        }

        String winnersJson = new ObjectMapper().writeValueAsString(winners);
        broadcastAllPlayers("Winners: " + winnersJson);

        bets.clear();
    }

    private void sendToPlayer(String UUID, String message) {
        sessions.stream()
                .filter(s -> UUID.equals(s.getAttributes().get("UUID")))
                .forEach(s -> {
                    try {
                        s.sendMessage(new TextMessage(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void broadcastAllPlayers(String message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
