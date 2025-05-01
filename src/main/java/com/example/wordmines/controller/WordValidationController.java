package com.example.wordmines.controller;

import com.example.wordmines.dto.PlacedLetterDto;
import com.example.wordmines.entity.GameBoard;
import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.model.ValidateWordRequest;
import com.example.wordmines.model.WordValidationResponse;
import com.example.wordmines.repository.GameBoardRepository;
import com.example.wordmines.repository.GameRoomRepository;
import com.example.wordmines.service.GameBoardService;
import com.example.wordmines.wordvalidation.DictionaryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/validate")
public class WordValidationController {

    private final DictionaryLoader dictionaryLoader;
    @Autowired
    private GameBoardRepository gameBoardRepository;
    @Autowired
    private GameRoomRepository gameRoomRepository;
    @Autowired
    private GameBoardService gameBoardService;

    public WordValidationController(DictionaryLoader dictionaryLoader) {
        this.dictionaryLoader = dictionaryLoader;
    }

    @PostMapping
    public ResponseEntity<WordValidationResponse> validateWord(@RequestBody ValidateWordRequest request) {
        System.out.println("istek keldi");
        String word = request.getMoves().stream()
                .map(PlacedLetterDto::getLetter)
                .collect(Collectors.joining());
        System.out.println("Kelime: "+word);
        boolean isValid = dictionaryLoader.isValidWord(word);
        System.out.println("Durum: "+isValid);
        int score = 0;
        if (isValid) {
            GameRoom room = gameRoomRepository.findById(request.getRoomId()).orElseThrow();
            GameBoard board = gameBoardRepository.findByRoom(room).orElseThrow();
            score = gameBoardService.calculateScore(request.getMoves(), board.getMatrixState());
        }

        WordValidationResponse response = new WordValidationResponse(isValid, score);
        return ResponseEntity.ok(response);
    }
}
