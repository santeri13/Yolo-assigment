package com.example.assigment.server;

public class Validator {
    public static boolean isValidBet(Bet bet) {
        return bet != null &&
                bet.getNickname() != null &&
                !bet.getNickname().trim().isEmpty() &&
                bet.getNumber() >= 1 && bet.getNumber() <= 10 &&
                bet.getAmount() > 0 &&
                bet.getUUID() != null &&
                !bet.getUUID().trim().isEmpty();
    }
}
