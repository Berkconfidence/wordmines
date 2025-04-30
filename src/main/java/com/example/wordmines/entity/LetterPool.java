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

    @Column(nullable = false)
    private String letter;     // Örn: "A", "Ç", "Ü"

    @Column(nullable = false)
    private int point;         // Harf puanı

    @Column(nullable = false)
    private int quantity;      // Toplam kaç tane var


}
