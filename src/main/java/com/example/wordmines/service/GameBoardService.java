package com.example.wordmines.service;

import com.example.wordmines.entity.*;
import com.example.wordmines.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameBoardService {

    private final GameBoardRepository gameBoardRepository;
    private final BoardInitializer boardInitializer;
    private final GameRoomRepository gameRoomRepository;
    private final PlayerLettersRepository playerLettersRepository;
    private final UserRepository userRepository;
    private final LetterBagRepository letterBagRepository;
    private final LetterService letterService;

    // Yeni tahta oluşturma (Artık matrisi otomatik oluşturuyor)
    public void createNewBoard(GameRoom room, User firstPlayer) {
        GameBoard board = new GameBoard();
        board.setRoom(room);
        board.setCurrentTurn(firstPlayer);
        board.setMatrixState(boardInitializer.createInitialBoard());
        gameBoardRepository.save(board);
    }

    // ID ile tahta getirme
    public GameBoard getBoardById(Long boardId) {
        return gameBoardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found!"));
    }

    @Transactional
    public void playMove(Long userId, Long roomId, List<String> usedLetters) {
        GameRoom room = gameRoomRepository.findById(roomId).orElseThrow();
        GameBoard board = gameBoardRepository.findByRoom(room).orElseThrow();

        if (!board.getCurrentTurn().getId().equals(userId)) {
            throw new IllegalStateException("Sıra sizde değil.");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        PlayerLetters playerLetters = playerLettersRepository.findByUserAndRoom(userOpt.get(), room)
                .orElseThrow();

        // 1. Kullanılan harfleri düş
        List<String> currentLetters = new ArrayList<>(playerLetters.getLetters());
        for (String letter : usedLetters) {
            currentLetters.remove(letter);
        }

        // 2. Eksik harf kadar letterBag'den yeni harf çek
        LetterBag bag = letterBagRepository.findByRoom(room).orElseThrow();
        Map<String, Integer> pool = bag.getRemainingLetters();

        int newLetterCount = 7 - currentLetters.size();
        List<String> newLetters = letterService.drawRandomLetters(pool, newLetterCount);
        currentLetters.addAll(newLetters);

        // 3. Güncellemeleri kaydet
        playerLetters.setLetters(currentLetters);
        bag.setRemainingLetters(pool);

        playerLettersRepository.save(playerLetters);
        letterBagRepository.save(bag);

        // 4. Sırayı değiştir
        User nextTurn = room.getPlayer1().getId().equals(userId)
                ? room.getPlayer2()
                : room.getPlayer1();

        board.setCurrentTurn(nextTurn);
        gameBoardRepository.save(board);
    }

}
