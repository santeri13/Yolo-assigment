package com.example.assigment.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bet {
    private String UUID;
    private final String nickname;
    private final int number;
    private final double amount;

    public Bet(
               @JsonProperty("nickname") String nickname,
               @JsonProperty("number") int number,
               @JsonProperty("amount") double amount){
        this.nickname = nickname;
        this.number = number;
        this.amount = amount;
    }

    public String getNickname() {
        return nickname;
    }

    public int getNumber() {
        return number;
    }

    public double getAmount() {
        return amount;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
