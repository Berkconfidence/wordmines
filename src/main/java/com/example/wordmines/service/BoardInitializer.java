package com.example.wordmines.service;

import com.example.wordmines.entity.GameBoard;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardInitializer {

    public List<List<GameBoard.Cell>> createInitialBoard() {
        String[][] template = {
                {"", "", "word*3", "", "", "letter*2", "", "", "", "letter*2", "", "", "word*3", "", ""},
                {"", "letter*3", "", "", "", "", "letter*2", "", "letter*2", "", "", "", "", "letter*3", ""},
                {"word*3", "", "", "", "", "", "", "word*2", "", "", "", "", "", "", "word*3"},
                {"", "", "", "word*2", "", "", "", "", "", "", "", "word*2", "", "", ""},
                {"", "", "", "", "letter*3", "", "", "", "", "", "letter*3", "", "", "", ""},
                {"letter*2", "", "", "", "", "letter*2", "", "", "", "letter*2", "", "", "", "", "letter*2"},
                {"", "letter*2", "", "", "", "", "letter*2", "", "letter*2", "", "", "", "", "letter*2", ""},
                {"", "", "word*2", "", "", "", "", "★", "", "", "", "", "word*2", "", ""},
                {"", "letter*2", "", "", "", "", "letter*2", "", "letter*2", "", "", "", "", "letter*2", ""},
                {"letter*2", "", "", "", "", "letter*2", "", "", "", "letter*2", "", "", "", "", "letter*2"},
                {"", "", "", "", "letter*3", "", "", "", "", "", "letter*3", "", "", "", ""},
                {"", "", "", "word*2", "", "", "", "", "", "", "", "word*2", "", "", ""},
                {"word*3", "", "", "", "", "", "", "word*2", "", "", "", "", "", "", "word*3"},
                {"", "letter*3", "", "", "", "", "letter*2", "", "letter*2", "", "", "", "", "letter*3", ""},
                {"", "", "word*3", "", "", "letter*2", "", "", "", "letter*2", "", "", "word*3", "", ""}
        };

        List<List<GameBoard.Cell>> board = new ArrayList<>();
        for (String[] row : template) {
            List<GameBoard.Cell> rowCells = new ArrayList<>();
            for (String cell : row) {
                rowCells.add(createCell(cell));
            }
            board.add(rowCells);
        }
        return board;
    }

    private GameBoard.Cell createCell(String cellValue) {
        GameBoard.Cell cell = new GameBoard.Cell();
        cell.setLetter(""); // Başlangıçta boş

        if (cellValue.contains("word")) {
            cell.setType("multiplier");
            cell.setMultiplier(cellValue);
        } else if (cellValue.contains("letter")) {
            cell.setType("multiplier");
            cell.setMultiplier(cellValue);
        } else if (cellValue.equals("★")) {
            cell.setType("mine");
            cell.setMultiplier(null);
        } else {
            cell.setType("normal");
            cell.setMultiplier(null);
        }

        cell.setMineActive(false);
        return cell;
    }

    /**
    public void updateCell(Long roomId, int row, int col, String letter) {
        GameBoard board = gameBoardRepository.findByRoomId(roomId)
                .orElseThrow();

        List<List<GameBoard.Cell>> matrix = board.getMatrixState();
        GameBoard.Cell cell = matrix.get(row).get(col);

        // Harf yerleştirme
        cell.setLetter(letter);

        // Mayın kontrolü
        if (cell.getType().equals("mine")) {
            cell.setMineActive(true);
            applyMineEffect(roomId, row, col);
        }

        gameBoardRepository.save(board);
    }
     **/

}
