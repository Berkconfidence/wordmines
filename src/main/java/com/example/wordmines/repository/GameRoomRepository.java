package com.example.wordmines.repository;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {

    @Query("""
        SELECT g FROM GameRoom g 
        WHERE g.status = :status AND 
             (g.player1.id = :userId OR g.player2.id = :userId)
    """)
    List<GameRoom> findByStatusAndPlayer(@Param("userId") Long userId,
                                         @Param("status") GameRoom.GameStatus status);

    long countByWinner(User user);
    long countByPlayer1OrPlayer2(User player1, User player2);
}
