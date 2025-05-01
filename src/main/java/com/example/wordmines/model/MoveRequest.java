package com.example.wordmines.model;

import com.example.wordmines.dto.PlacedLetterDto;
import lombok.Data;

import java.util.List;

@Data
public class MoveRequest {
    private Long roomId;
    private List<PlacedLetterDto> moves;

    // getter/setter
}
