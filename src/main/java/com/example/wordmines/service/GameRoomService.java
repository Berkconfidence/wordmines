package com.example.wordmines.service;

import com.example.wordmines.dto.GameRoomDto;
import com.example.wordmines.entity.GameBoard;
import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.User;
import com.example.wordmines.repository.GameBoardRepository;
import com.example.wordmines.repository.GameRoomRepository;
import com.example.wordmines.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameRoomService {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameBoardRepository gameBoardRepository;


    public GameRoom createRoom(Long roomId, Long userId, Long opponentId, String duration) {

        Optional<User> user1 = userRepository.findById(userId);
        Optional<User> user2 = userRepository.findById(opponentId);

        if(user1.isEmpty() || user2.isEmpty() || duration == null)
            throw new IllegalArgumentException("Bilgiler eksik");

        GameRoom room = new GameRoom();
        room.setRoomId(roomId);
        room.setPlayer1(user1.get());
        room.setPlayer2(user2.get());
        room.setGameDuration(duration);
        room.setStatus(GameRoom.GameStatus.ACTIVE);
        room.setCreatedAt(new Date());

        return gameRoomRepository.save(room);
    }

    public ResponseEntity<List<GameRoomDto>> getActiveGames(Long userId) {
        List<GameRoom> activeGames = gameRoomRepository
                .findByStatusAndPlayer(userId, GameRoom.GameStatus.ACTIVE);

        List<GameRoomDto> activeGamesDto = activeGames.stream()
                .map(game -> {
                    GameRoomDto dto = new GameRoomDto();
                    try {
                        dto.setRoomId(game.getRoomId());
                        dto.setPlayer1Id(game.getPlayer1() != null ? game.getPlayer1().getId() : null);
                        dto.setPlayer2Id(game.getPlayer2() != null ? game.getPlayer2().getId() : null);
                        dto.setGameDuration(game.getGameDuration());
                        dto.setCreatedAt(game.getCreatedAt());
                        dto.setFinishedAt(game.getFinishedAt());
                        Optional<GameBoard> board = gameBoardRepository.findByRoom(game);
                        dto.setCurrentTurn(board.map(b -> b.getCurrentTurn().getId()).orElse(null));
                    } catch (Exception e) {
                        System.out.println("Hata oluştu: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeGamesDto);
    }

}
