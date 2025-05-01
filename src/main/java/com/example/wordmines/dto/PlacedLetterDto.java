package com.example.wordmines.dto;

import lombok.Data;

@Data
public class PlacedLetterDto {
    private String letter;
    private int points;
    private Position position;

    @Data
    public static class Position {
        private int row;
        private int col;

    }

}