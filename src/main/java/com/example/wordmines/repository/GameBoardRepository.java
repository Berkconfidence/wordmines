package com.example.wordmines.repository;

import com.example.wordmines.entity.GameBoard;
import com.example.wordmines.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;

public interface GameBoardRepository extends JpaRepository<GameBoard, Long> {

    ScopedValue<Object> findByRoom(GameRoom room);
}
