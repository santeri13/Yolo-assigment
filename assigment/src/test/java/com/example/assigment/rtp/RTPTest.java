package com.example.assigment.rtp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAdder;

@SpringBootTest
class RTPTest {

    private static final int TOTAL_GAMES = 1_000_000;
    private static final int THREADS = 24;
    private static final double STAKE = 1.0;
    private static final double MULTIPLIER = 9.9;
    private static final int NUMBER_RANGE = 10;

    @Test
    void rtpTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        DoubleAdder totalWon = new DoubleAdder();
        CountDownLatch latch = new CountDownLatch(TOTAL_GAMES);

        for (int i = 0; i < TOTAL_GAMES; i++) {
            executor.submit(() -> {
                try {
                    int playerNumber = new Random().nextInt(NUMBER_RANGE) + 1;
                    int winNumber = new Random().nextInt(NUMBER_RANGE) + 1;
                    if (playerNumber == winNumber) {
                        totalWon.add(STAKE * MULTIPLIER);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        double totalBet = TOTAL_GAMES * STAKE;
        double rtp = (totalWon.doubleValue() / totalBet) * 100;

        System.out.printf("Total bet: %.2f, Total won: %.2f, RTP: %.2f%%%n", totalBet, totalWon.doubleValue(), rtp);
    }

}
