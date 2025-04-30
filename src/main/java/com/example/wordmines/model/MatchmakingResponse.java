package com.example.wordmines.model;

import lombok.Data;

@Data
public class MatchmakingResponse {

    private Long roomId;
    private String opponentId;
    private boolean isInitiator;

    public MatchmakingResponse(Long roomId, String opponentId, boolean isInitiator) {
        this.roomId = roomId;
        this.opponentId = opponentId;
        this.isInitiator = isInitiator;
    }

    // Getter ve setter metodları
}
