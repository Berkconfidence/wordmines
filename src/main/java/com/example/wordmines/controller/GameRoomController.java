package com.example.wordmines.controller;


import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.LetterBag;
import com.example.wordmines.entity.PlayerScore;
import com.example.wordmines.entity.User;
import com.example.wordmines.repository.GameRoomRepository;
import com.example.wordmines.repository.LetterBagRepository;
import com.example.wordmines.repository.PlayerScoreRepository;
import com.example.wordmines.service.GameBoardService;
import com.example.wordmines.service.GameRoomService;
import com.example.wordmines.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gameroom")
public class GameRoomController {


    private final GameRoomService gameRoomService;
    private final GameBoardService gameBoardService;
    private final LetterService letterService;
    private final GameRoomRepository gameRoomRepository;
    private final PlayerScoreRepository playerScoreRepository;
    private final LetterBagRepository letterBagRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestParam Long roomId, @RequestParam String userId,
                                        @RequestParam String opponentId, @RequestParam String duration) {
        try {
            System.out.println("Oda kuruluyor");
            Long userIdLong = Long.parseLong(userId);
            Long opponentIdLong = Long.parseLong(opponentId);

            GameRoom room = gameRoomService.createRoom(roomId, userIdLong, opponentIdLong, duration);
            if (room == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı adı veya şifre hatalı");
            } else {
                gameBoardService.createNewBoard(room, room.getPlayer1(), room.getPlayer2());
                LetterBag bag = letterService.createLetterBag(room);
                letterService.assignInitialLetters(room, room.getPlayer1(), bag);
                letterService.assignInitialLetters(room, room.getPlayer2(), bag);
            }
            return ResponseEntity.ok(room);
        } catch (NumberFormatException nfe) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Geçersiz kullanıcı ID formatı");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Sunucu hatası: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveGames(@RequestParam String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            return gameRoomService.getActiveGames(userIdLong);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Geçersiz kullanıcı ID formatı");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getRoomStatus(@RequestParam Long roomId) {
        GameRoom room = gameRoomRepository.findById(roomId).orElse(null);
        if (room == null) return ResponseEntity.notFound().build();

        User player1 = room.getPlayer1();
        User player2 = room.getPlayer2();

        // Player 1 skoru
        int player1Score = playerScoreRepository.findByUserAndRoom(player1, room)
                .map(PlayerScore::getScore)
                .orElse(0);

        // Player 2 skoru
        int player2Score = playerScoreRepository.findByUserAndRoom(player2, room)
                .map(PlayerScore::getScore)
                .orElse(0);

        // Kalan harf sayısı (oyundaki torbada kalan harfler)
        int remainingLetters = letterBagRepository.findByRoom(room)
                .map(bag -> bag.getRemainingLetters().values().stream().mapToInt(Integer::intValue).sum())
                .orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("player1", Map.of(
                "username", player1.getUsername(),
                "score", player1Score
        ));
        result.put("player2", Map.of(
                "username", player2.getUsername(),
                "score", player2Score
        ));
        result.put("remainingLetters", remainingLetters);

        System.out.println("kalan harf: "+remainingLetters);

        return ResponseEntity.ok(result);
    }


}
