package com.example.wordmines.controller;

import com.example.wordmines.entity.GameBoard;
import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.User;
import com.example.wordmines.service.GameBoardService;
import com.example.wordmines.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gameboard")
public class GameBoardController {

    @Autowired
    private GameBoardService gameBoardService;

    @PostMapping("/play")
    public ResponseEntity<?> playMove(@RequestParam String userId, @RequestParam String roomId, @RequestBody List<String> usedLetters) {
        try {
            Long userIdLong = Long.parseLong(userId);
            Long roomIdLong = Long.parseLong(roomId);

            gameBoardService.playMove(userIdLong, roomIdLong, usedLetters);
            return ResponseEntity.ok("Hamle işlendi ve sıra geçti.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}