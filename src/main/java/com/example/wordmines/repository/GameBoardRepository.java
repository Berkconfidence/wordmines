package com.example.wordmines.repository;

import com.example.wordmines.entity.GameBoard;
import com.example.wordmines.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameBoardRepository extends JpaRepository<GameBoard, Long> {

    Optional<GameBoard> findByRoom(GameRoom room);

    Optional<GameBoard> findByRoomRoomId(Long roomId);

}
