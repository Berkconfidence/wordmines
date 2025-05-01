package com.example.wordmines.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="letter_pool")
public class LetterPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(5) COLLATE utf8mb4_turkish_ci")
    private String letter;

    @Column(nullable = false)
    private int point; // Harf puanı

    @Column(nullable = false)
    private int quantity; // Toplam kaç tane var

}
