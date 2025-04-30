package com.example.wordmines.service;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.User;
import com.example.wordmines.repository.GameRoomRepository;
import com.example.wordmines.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class GameRoomService {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private UserRepository userRepository;


    public GameRoom createRoom(Long roomId, Long userId, Long opponentId, String duration) {

        Optional<User> user1 = userRepository.findById(userId);
        Optional<User> user2 = userRepository.findById(opponentId);
        System.out.println("1");

        if(user1.isEmpty() || user2.isEmpty() || duration == null)
            throw new IllegalArgumentException("Bilgiler eksik");

        System.out.println("2");
        GameRoom room = new GameRoom();
        room.setRoomId(roomId);
        room.setPlayer1(user1.get());
        room.setPlayer2(user2.get());
        room.setGameDuration(duration);
        room.setStatus(GameRoom.GameStatus.ACTIVE);
        room.setCreatedAt(new Date());

        System.out.println("3");
        return gameRoomRepository.save(room);
    }
}
