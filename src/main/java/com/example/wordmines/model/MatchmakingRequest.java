package com.example.wordmines.model;

import lombok.Data;

@Data
public class MatchmakingRequest {
    private String userId;
    private String gameDuration;
    private long timestamp;

    public MatchmakingRequest(String userId, String gameDuration) {
        this.userId = userId;
        this.gameDuration = gameDuration;
        this.timestamp = System.currentTimeMillis();
    }

}
