package com.example.wordmines.model;

import lombok.Data;

@Data
public class GameOptions {
    private String duration;
    private String userId;

    public GameOptions(String duration, String userId) {
        this.duration = duration;
        this.userId = userId;
    }
}