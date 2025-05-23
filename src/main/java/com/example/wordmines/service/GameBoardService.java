package com.example.wordmines.service;

import com.example.wordmines.dto.PlacedLetterDto;
import com.example.wordmines.entity.*;
import com.example.wordmines.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameBoardService {

    private final GameBoardRepository gameBoardRepository;
    private final BoardInitializer boardInitializer;
    private final GameRoomRepository gameRoomRepository;
    private final PlayerLettersRepository playerLettersRepository;
    private final LetterBagRepository letterBagRepository;
    private final LetterService letterService;
    private final PlayerScoreRepository playerScoreRepository;
    private final MoveHistoryRepository moveHistoryRepository;


    public void createNewBoard(GameRoom room, User firstPlayer, User secondPlayer) {
        GameBoard board = new GameBoard();
        board.setRoom(room);

        User currentTurn = new Random().nextBoolean() ? firstPlayer : secondPlayer;
        board.setCurrentTurn(currentTurn);

        board.setLastMoveAt(new Date()); // Oyunun oluşturulduğu an
        board.setFirstMove(true);

        board.setMatrixState(boardInitializer.createInitialBoard());
        gameBoardRepository.save(board);
    }


    @Transactional
    public void processMove(Long roomId, List<PlacedLetterDto> moves) {
        GameRoom room = gameRoomRepository.findById(roomId).orElseThrow();
        GameBoard board = gameBoardRepository.findByRoom(room).orElseThrow();
        User currentUser = board.getCurrentTurn();

        // Zaman kontrolü
        long now = System.currentTimeMillis();
        long lastMove = board.getLastMoveAt().getTime();
        long diffMinutes = (now - lastMove) / (60 * 1000);

        int limit = board.isFirstMove() ? 60 : Integer.parseInt(room.getGameDuration()); // ilk hamle 60 dk
        if (diffMinutes > limit) {
            throw new IllegalStateException("Süre dolmuş, hamle yapılamaz.");
        }

        PlayerLetters playerLetters = playerLettersRepository
                .findByUserAndRoom(currentUser, room)
                .orElseThrow();

        // 1. Kullanılan harfleri düş
        List<String> current = new ArrayList<>(playerLetters.getLetters());
        for (PlacedLetterDto dto : moves) {
            current.remove(dto.getLetter());
        }

        // 2. Harfleri matrixState'e yerleştir
        List<List<GameBoard.Cell>> matrix = board.getMatrixState();
        for (PlacedLetterDto dto : moves) {
            int r = dto.getPosition().getRow();
            int c = dto.getPosition().getCol();
            GameBoard.Cell cell = matrix.get(r).get(c);
            cell.setLetter(dto.getLetter());
        }

        // 3. Skoru hesapla
        int baseScore = calculateScore(moves, matrix);

        // 4. PlayerScore güncelle
        PlayerScore score = playerScoreRepository.findByUserAndRoom(currentUser, room)
                .orElseGet(() -> {
                    PlayerScore ps = new PlayerScore();
                    ps.setRoom(room);
                    ps.setUser(currentUser);
                    ps.setScore(0);
                    return ps;
                });
        score.setScore(score.getScore() + baseScore);
        playerScoreRepository.save(score);

        // 5. MoveHistory kaydet
        String word = moves.stream().map(PlacedLetterDto::getLetter).collect(Collectors.joining());
        MoveHistory history = new MoveHistory();
        history.setRoom(room);
        history.setUser(currentUser);
        history.setWord(word);
        history.setBaseScore(baseScore);
        history.setFinalScore(baseScore); // Şimdilik aynı
        history.setPlayedAt(new Date());
        history.setTurnNumber(moveHistoryRepository.countByRoom(room) + 1);
        moveHistoryRepository.save(history);

        // 6. Eksik harfleri tamamla
        int eksik = 7 - current.size();
        LetterBag bag = letterBagRepository.findByRoom(room).orElseThrow();
        List<String> yeniler = letterService.drawRandomLetters(bag.getRemainingLetters(), eksik);
        current.addAll(yeniler);
        playerLetters.setLetters(current);
        playerLettersRepository.save(playerLetters);

        // 7. Sırayı değiştir
        User next = currentUser.getId().equals(room.getPlayer1().getId())
                ? room.getPlayer2() : room.getPlayer1();
        board.setCurrentTurn(next);

        // 8. Süre güncelle
        board.setLastMoveAt(new Date());
        board.setFirstMove(false);

        gameBoardRepository.save(board);
    }


    public int calculateScore(List<PlacedLetterDto> moves, List<List<GameBoard.Cell>> matrix) {
        int totalLetterScore = 0;
        int wordMultiplier = 1;

        for (PlacedLetterDto move : moves) {
            int r = move.getPosition().getRow();
            int c = move.getPosition().getCol();
            GameBoard.Cell cell = matrix.get(r).get(c);

            int baseLetterScore = letterService.getPointForLetter(move.getLetter());
            int letterScore = baseLetterScore;

            // multiplier varsa uygula
            String multiplier = cell.getMultiplier();
            if (multiplier != null) {
                if (multiplier.startsWith("letter*")) {
                    int factor = Integer.parseInt(multiplier.substring(7));
                    letterScore *= factor;
                } else if (multiplier.startsWith("word*")) {
                    int factor = Integer.parseInt(multiplier.substring(5));
                    wordMultiplier *= factor;
                }
            }

            totalLetterScore += letterScore;
        }

        return totalLetterScore * wordMultiplier;
    }



}
