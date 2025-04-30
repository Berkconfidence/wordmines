package com.example.wordmines.repository;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.LetterBag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LetterBagRepository extends JpaRepository<LetterBag, Long> {

    Optional<LetterBag> findByRoom(GameRoom room);
}