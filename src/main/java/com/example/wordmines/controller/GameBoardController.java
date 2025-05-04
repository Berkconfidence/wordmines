package com.example.wordmines.controller;

import com.example.wordmines.entity.GameBoard;
import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.User;
import com.example.wordmines.model.MoveRequest;
import com.example.wordmines.repository.GameBoardRepository;
import com.example.wordmines.repository.GameRoomRepository;
import com.example.wordmines.service.GameBoardService;
import com.example.wordmines.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/gameboard")
public class GameBoardController {

    @Autowired
    private GameBoardService gameBoardService;

    @Autowired
    private GameBoardRepository gameBoardRepository;

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/turn")
    public ResponseEntity<?> currentTurn(@RequestParam String roomId) {
        try {
            Long roomIdLong = Long.parseLong(roomId);
            Optional<GameRoom> room = gameRoomRepository.findById(roomIdLong);
            Optional<GameBoard> board = gameBoardRepository.findByRoom(room.get());
            return ResponseEntity.ok(board.get().getCurrentTurn().getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/matrix")
    public ResponseEntity<?> getMatrix(@RequestParam String roomId) {
        Long roomIdLong = Long.parseLong(roomId);
        Optional<GameBoard> gameBoardOpt = gameBoardRepository.findByRoomRoomId(roomIdLong);
        if (gameBoardOpt.isPresent()) {
            List<List<GameBoard.Cell>> matrix = gameBoardOpt.get().getMatrixState();
            return ResponseEntity.ok(matrix);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tahta bulunamadı");
        }
    }

    @GetMapping("/moveinfo")
    public ResponseEntity<Map<String, Object>> getMoveInfo(@RequestParam Long roomId) {
        GameRoom room = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        GameBoard board = gameBoardRepository.findByRoom(room)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("lastMoveAt", board.getLastMoveAt());
        response.put("isFirstMove", board.isFirstMove());

        return ResponseEntity.ok(response);
    }

    @MessageMapping("/move")
    public void handleMove(MoveRequest request) {
        gameBoardService.processMove(request.getRoomId(), request.getMoves());

        // Güncellenmiş board'u gönder
        GameBoard board = gameBoardRepository.findByRoomRoomId(request.getRoomId()).orElseThrow();
        messagingTemplate.convertAndSend(
                "/topic/board-update/" + request.getRoomId(),
                board.getMatrixState()
        );
    }

}