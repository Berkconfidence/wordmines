package com.example.wordmines.repository;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.MoveHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveHistoryRepository extends JpaRepository<MoveHistory,Long> {
    int countByRoom(GameRoom room);
}
