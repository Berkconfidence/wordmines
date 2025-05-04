package com.example.wordmines.dto;

import lombok.Data;

import java.util.Date;

@Data
public class GameRoomDto {

    private Long roomId;
    private Long player1Id;
    private Long player2Id;
    private String gameDuration;
    private Date createdAt;
    private Date finishedAt;
    private int player1Score;
    private int player2Score;
    private Long currentTurn;
    private Long winnerId;

}
