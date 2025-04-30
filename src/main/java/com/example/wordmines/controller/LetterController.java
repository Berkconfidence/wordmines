package com.example.wordmines.controller;

import com.example.wordmines.entity.GameRoom;
import com.example.wordmines.entity.PlayerLetters;
import com.example.wordmines.entity.User;
import com.example.wordmines.repository.GameRoomRepository;
import com.example.wordmines.repository.PlayerLettersRepository;
import com.example.wordmines.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letters")
public class LetterController {

    private final PlayerLettersRepository playerLettersRepository;
    private final UserRepository userRepository;
    private final GameRoomRepository gameRoomRepository;

    @GetMapping
    public ResponseEntity<?> getPlayerLetters(@RequestParam String userId, @RequestParam String roomId) {
        Long userIdLong = Long.parseLong(userId);
        Long roomIdLong = Long.parseLong(roomId);

        Optional<User> userOpt = userRepository.findById(userIdLong);
        Optional<GameRoom> roomOpt = gameRoomRepository.findById(roomIdLong);

        if (userOpt.isEmpty() || roomOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kullanıcı veya Oda bulunamadı");
        }
        System.out.println("User: "+userOpt.get()+" "+"room: "+roomOpt.get());

        Optional<PlayerLetters> playerLetters = playerLettersRepository
                .findByUserAndRoom(userOpt.get(), roomOpt.get());

        if (playerLetters.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Harfler bulunamadı");
        }

        return ResponseEntity.ok(playerLetters.get().getLetters());
    }



    @GetMapping("/deneme")
    public void deneme() {
        System.out.println("merhaba");
    }
}

