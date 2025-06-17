package com.comeon.gamecounter.gateway.POJO;

public class LoginRequest {
    private int playerId;
    private String password;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
