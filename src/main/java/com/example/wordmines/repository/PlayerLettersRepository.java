package com.example.wordmines.repository;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.PlayerLetters;
import com.example.wordmines.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerLettersRepository extends JpaRepository<PlayerLetters, Long> {

    Optional<PlayerLetters> findByUserAndRoom(User user, GameRoom room);
}
