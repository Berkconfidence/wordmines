package com.example.wordmines.controller;


import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.LetterBag;
import com.example.wordmines.service.GameBoardService;
import com.example.wordmines.service.GameRoomService;
import com.example.wordmines.service.LetterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gameroom")
public class GameRoomController {

    @Autowired
    private GameRoomService gameRoomService;

    @Autowired
    private GameBoardService gameBoardService;

    @Autowired
    private LetterService letterService;

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
                System.out.println("4");
                gameBoardService.createNewBoard(room, room.getPlayer1());
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


}
