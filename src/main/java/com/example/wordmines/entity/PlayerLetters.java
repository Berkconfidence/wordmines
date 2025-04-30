package com.example.wordmines.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "player_letters")
public class PlayerLetters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private GameRoom room;

    @ElementCollection
    @CollectionTable(name = "player_letters_list", joinColumns = @JoinColumn(name = "player_letters_id"))
    @Column(name = "letter")
    private List<String> letters; // ["A", "E", "L", "T", "İ", "R", "K"]
}

