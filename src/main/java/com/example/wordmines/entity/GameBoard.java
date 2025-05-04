package com.example.wordmines.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "game_boards")
@Data
public class GameBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @OneToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false, unique = true)
    private GameRoom room;

    @ManyToOne
    @JoinColumn(name = "current_turn", referencedColumnName = "id")
    private User currentTurn;

    @Column(name = "remaining_letters", columnDefinition = "INT DEFAULT 86")
    private Integer remainingLetters = 86;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMoveAt;

    @Column(name = "firs_move")
    private boolean isFirstMove = true;

    // Yeni: Matrisi daha yapılandırılmış şekilde saklamak için
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "matrix_state", columnDefinition = "JSON")
    private List<List<Cell>> matrixState;


    // Hücre yapısını tanımlayan iç sınıf
    @Data
    public static class Cell {
        private String type;
        private String letter; // Oynanan harf (boşsa "")
        private String multiplier; // "word*3", "letter*2" vb.
        private boolean isMineActive; // Mayın tetiklendi mi?
    }
}