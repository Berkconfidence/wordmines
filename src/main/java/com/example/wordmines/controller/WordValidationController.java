package com.example.wordmines.controller;

import com.example.wordmines.wordvalidation.DictionaryLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
public class WordValidationController {

    private final DictionaryLoader dictionaryLoader;

    public WordValidationController(DictionaryLoader dictionaryLoader) {
        this.dictionaryLoader = dictionaryLoader;
    }

    @GetMapping
    public ResponseEntity<Boolean> validate(@RequestParam String word) {
        return ResponseEntity.ok(dictionaryLoader.isValidWord(word));
    }
}
