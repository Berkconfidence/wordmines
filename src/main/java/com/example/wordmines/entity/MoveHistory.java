package com.example.wordmines.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "move_history")
public class MoveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GameRoom room;

    @ManyToOne
    private User user;

    private String word;

    private int baseScore;

    private int finalScore;

    private int turnNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date playedAt;
}
