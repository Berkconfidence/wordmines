package com.example.wordmines.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordValidationResponse {
    private boolean isValid;
    private int score;
}
