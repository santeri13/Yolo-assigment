package com.example.assigment.server;

public class WinResult {
    private final String nickname;
    private final double win;
    private String UUID;

    public WinResult(String nickname, double win, String UUID) {
        this.nickname = nickname;
        this.win = win;
        this.UUID = UUID;
    }

    public String getNickname() {
        return nickname;
    }

    public double getWin() {
        return win;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
