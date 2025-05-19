package com.example.assigment.server;

public class WinResult {
    private final String nickname;
    private final double win;

    public WinResult(String nickname, double win) {
        this.nickname = nickname;
        this.win = win;
    }

    public String getNickname() {
        return nickname;
    }

    public double getWin() {
        return win;
    }
}
