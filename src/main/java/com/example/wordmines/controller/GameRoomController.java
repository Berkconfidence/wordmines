package com.example.wordmines.controller;


import com.example.wordmines.entity.*;
import com.example.wordmines.repository.*;
import com.example.wordmines.service.GameBoardService;
import com.example.wordmines.service.GameRoomService;
import com.example.wordmines.service.LetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private final GameBoardRepository gameBoardRepository;
    private final UserRepository userRepository;

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

    @GetMapping("/finished")
    public ResponseEntity<?> getFinishedGames(@RequestParam String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            return gameRoomService.getFinishedGames(userIdLong);
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
                "score", player1Score,
                "id", player1.getId()
        ));
        result.put("player2", Map.of(
                "username", player2.getUsername(),
                "score", player2Score,
                "id", player1.getId()
        ));
        result.put("remainingLetters", remainingLetters);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/scores")
    public ResponseEntity<?> getScoresByRoom(@RequestParam Long roomId) {
        Optional<GameRoom> optionalRoom = gameRoomRepository.findById(roomId);
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        }

        GameRoom room = optionalRoom.get();
        List<Map<String, Object>> result = new ArrayList<>();

        for (User player : Arrays.asList(room.getPlayer1(), room.getPlayer2())) {
            PlayerScore score = playerScoreRepository.findByUserAndRoom(player, room)
                    .orElseGet(() -> {
                        PlayerScore ps = new PlayerScore();
                        ps.setUser(player);
                        ps.setRoom(room);
                        ps.setScore(0);
                        return ps;
                    });

            Map<String, Object> playerData = new HashMap<>();
            playerData.put("userId", player.getId());
            playerData.put("username", player.getUsername());
            playerData.put("score", score.getScore());
            result.add(playerData);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/successrate")
    public ResponseEntity<?> getSuccessRate(@RequestParam Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        User user = optionalUser.get();

        // Kullanıcının kazandığı oyun sayısı
        long wonGames = gameRoomRepository.countByWinner(user);

        // Kullanıcının dahil olduğu oyunlar (player1 veya player2)
        long totalGames = gameRoomRepository.countByPlayer1OrPlayer2(user, user);

        if (totalGames == 0) {
            return ResponseEntity.ok(Map.of(
                    "userId", userId,
                    "username", user.getUsername(),
                    "successRate", 0,
                    "totalGames", 0,
                    "wonGames", 0
            ));
        }

        double successRate = (double) wonGames / totalGames * 100;

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "username", user.getUsername(),
                "successRate", successRate,
                "totalGames", totalGames,
                "wonGames", wonGames
        ));
    }

    @PostMapping("/finish")
    public ResponseEntity<?> finishGame(@RequestParam Long roomId) {
        Optional<GameRoom> optionalRoom = gameRoomRepository.findById(roomId);
        if (optionalRoom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Room not found"));
        }

        GameRoom room = optionalRoom.get();

        Optional<GameBoard> optionalBoard = gameBoardRepository.findByRoom(room);
        if (optionalBoard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Game board not found"));
        }

        GameBoard board = optionalBoard.get();
        User currentTurnUser = board.getCurrentTurn();

        User winner;
        User loser;
        if (room.getPlayer1().getId().equals(currentTurnUser.getId())) {
            winner = room.getPlayer2();
            loser = room.getPlayer1();
        } else {
            winner = room.getPlayer1();
            loser = room.getPlayer2();
        }

        room.setWinner(winner);
        room.setStatus(GameRoom.GameStatus.FINISHED);
        room.setFinishedAt(new Date());
        gameRoomRepository.save(room);

        // JSON olarak winner ve loser bilgisi dön
        Map<String, Object> response = new HashMap<>();
        response.put("winnerId", winner.getId());
        response.put("winnerUsername", winner.getUsername());
        response.put("loserId", loser.getId());
        response.put("loserUsername", loser.getUsername());
        response.put("roomId", roomId);
        response.put("status", "FINISHED");

        return ResponseEntity.ok(response);
    }


}
