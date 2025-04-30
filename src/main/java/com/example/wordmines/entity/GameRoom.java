package com.example.wordmines.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "game_rooms")
@Data
public class GameRoom {

    @Id
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "player1_id", referencedColumnName = "id", nullable = false)
    private User player1;

    @ManyToOne
    @JoinColumn(name = "player2_id", referencedColumnName = "id")
    private User player2;

    @Column(name = "game_duration", nullable = false, length = 20)
    private String gameDuration; // "2m", "5m", "12h", "24h"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('WAITING', 'ACTIVE', 'FINISHED') DEFAULT 'WAITING'")
    private GameStatus status = GameStatus.WAITING;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;

    public enum GameStatus {
        WAITING,
        ACTIVE,
        FINISHED
    }
}