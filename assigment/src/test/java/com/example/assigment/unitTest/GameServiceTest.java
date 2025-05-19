package com.example.assigment.unitTest;

import com.example.assigment.server.Bet;
import com.example.assigment.server.WinResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private static final double WIN_MULTIPLIER = 9.9;

    @Test
    void testCalculateWin() {
        Bet winBet = new Bet("player1", 5, 100);
        int winNumber = 5;
        String uuid = UUID.randomUUID().toString();
        winBet.setUUID(uuid);

        Map<String, WinResult> winners = calculateWin(List.of(winBet), winNumber);

        Map.Entry<String, WinResult> entry = winners.entrySet().iterator().next();
        assertEquals(1, winners.size());
        assertEquals("player1", entry.getValue().getNickname());
        assertEquals(990.0, entry.getValue().getWin());
    }

    @Test
    void testNoWin() {
        Bet losingBet = new Bet("player1", 2,100);
        int winNumber = 5;

        Map<String, WinResult> winners = calculateWin(List.of(losingBet), winNumber);

        assertTrue(winners.isEmpty());
    }

    @Test
    void testTwoPlayerBets() {
        List<Bet> bets = new ArrayList<>();

        Bet winBet = new Bet("Alice", 7, 100);
        winBet.setUUID("uuid-1");
        bets.add(winBet);

        Bet losingBet = new Bet("Bob", 3, 50);
        losingBet.setUUID("uuid-2");
        bets.add(losingBet);

        int winNumber = 7;
        Map<String, WinResult> winners = calculateWin(bets,winNumber);

        assertEquals(1, winners.size());

        Map.Entry<String, WinResult> entry = winners.entrySet().iterator().next();
        assertEquals("Alice", entry.getValue().getNickname());
        assertEquals(990.0, entry.getValue().getWin(), 0.001);


        assertFalse(winners.values().stream().anyMatch(w -> w.getNickname().equals("Bob")));
    }

    public Map<String, WinResult> calculateWin(List<Bet> bets, int winningNumber) {
        Map<String, WinResult> winners = new ConcurrentHashMap<>();

        for (Bet bet : bets) {
            if (bet.getNumber() == winningNumber) {
                int win = (int) ((int)bet.getAmount() * WIN_MULTIPLIER);
                winners.put(bet.getUUID(),new WinResult(bet.getNickname(), win));
            }
        }

        return winners;
    }
}
