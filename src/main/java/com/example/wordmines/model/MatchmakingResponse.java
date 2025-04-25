package com.example.wordmines.model;

import lombok.Data;

@Data
public class MatchmakingResponse {
    private String roomId;
    private String opponentId;

    public MatchmakingResponse(String roomId, String opponentId) {
        this.roomId = roomId;
        this.opponentId = opponentId;
    }

    // Getter ve setter metodları
}
