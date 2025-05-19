package com.example.assigment.unitTest;

import com.example.assigment.server.Bet;
import com.example.assigment.server.WinResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private static final double WIN_MULTIPLIER = 9.9;

    @Test
    void testCalculateWin() {
        Bet winBet = new Bet("player1", 5, 100);
        int winNumber = 5;

        List<WinResult> winners = calculateWin(List.of(winBet), winNumber);

        assertEquals(1, winners.size());
        assertEquals("player1", winners.getFirst().getNickname());
        assertEquals(990.0, winners.getFirst().getWin());
    }

    @Test
    void testNoWin() {
        Bet losingBet = new Bet("player1", 2,100);
        int winNumber = 5;

        List<WinResult> winners = calculateWin(List.of(losingBet), winNumber);

        assertTrue(winners.isEmpty());
    }

    @Test
    void testTwoPlayerBets() {
        List<Bet> bets = new ArrayList<>();

        Bet winBet = new Bet("Alice", 7, 100.0);
        winBet.setUUID("uuid-1");
        bets.add(winBet);

        Bet losingBet = new Bet("Bob", 3, 50.0);
        losingBet.setUUID("uuid-2");
        bets.add(losingBet);

        int winNumber = 7;
        List<WinResult> winners = calculateWin(bets,winNumber);

        assertEquals(1, winners.size());

        WinResult winner = winners.get(0);
        assertEquals("Alice", winner.getNickname());
        assertEquals(990.0, winner.getWin(), 0.001);


        assertFalse(winners.stream().anyMatch(w -> w.getNickname().equals("Bob")));
    }

    public List<WinResult> calculateWin(List<Bet> bets, int winningNumber) {
        List<WinResult> winners = new ArrayList<>();

        for (Bet bet : bets) {
            if (bet.getNumber() == winningNumber) {
                double win = bet.getAmount() * WIN_MULTIPLIER;
                WinResult winner = new WinResult(bet.getNickname(), win, bet.getUUID());
                winners.add(winner);
            }
        }

        return winners;
    }
}
