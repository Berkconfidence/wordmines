package com.example.wordmines.repository;

import com.example.wordmines.entity.LetterPool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LetterPoolRepository extends JpaRepository<LetterPool, Long> {
    Optional<LetterPool> findByLetter(String letter);

}
