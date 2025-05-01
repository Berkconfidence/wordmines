package com.example.wordmines.repository;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.PlayerScore;
import com.example.wordmines.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScore, Long> {
    Optional<PlayerScore> findByUserAndRoom(User user, GameRoom room);
}