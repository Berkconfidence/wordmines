package com.example.wordmines.model;

import com.example.wordmines.dto.PlacedLetterDto;
import lombok.Data;

import java.util.List;

@Data
public class ValidateWordRequest {
    private Long roomId;
    private List<PlacedLetterDto> moves; // harf ve pozisyon bilgisi
}