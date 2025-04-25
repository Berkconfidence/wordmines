package com.example.wordmines.model;

import lombok.Data;

@Data
public class MatchmakingUpdate {
    private int queueLength;
    private int estimatedTime;

    public MatchmakingUpdate(int queueLength, int estimatedTime) {
        this.queueLength = queueLength;
        this.estimatedTime = estimatedTime;
    }

}