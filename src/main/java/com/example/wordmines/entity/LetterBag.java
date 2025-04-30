package com.example.wordmines.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Data
@Table(name="letter_bag")
public class LetterBag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id")
    private GameRoom room;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private Map<String, Integer> remainingLetters;
    // {"A": 11, "B": 2, "Ç": 2, ...} → kullanılabilir harf adedi
}
