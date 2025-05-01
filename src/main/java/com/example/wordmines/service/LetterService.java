package com.example.wordmines.service;

import com.example.wordmines.entity.*;
import com.example.wordmines.repository.LetterBagRepository;
import com.example.wordmines.repository.LetterPoolRepository;
import com.example.wordmines.repository.PlayerLettersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterService {

    private final LetterPoolRepository letterPoolRepository;
    private final LetterBagRepository letterBagRepository;
    private final PlayerLettersRepository playerLettersRepository;

    private final Random random = new Random();

    // Oyun başlarken ortak harf havuzunu oluştur
    public LetterBag createLetterBag(GameRoom room) {
        List<LetterPool> baseLetters = letterPoolRepository.findAll();
        System.out.println("Bulunan harf sayısı: " + baseLetters.size());

        // Map<String, Integer> olarak kopyala
        Map<String, Integer> letterCounts = baseLetters.stream()
                .collect(Collectors.toMap(LetterPool::getLetter, LetterPool::getQuantity));

        LetterBag bag = new LetterBag();
        bag.setRoom(room);
        bag.setRemainingLetters(letterCounts);

        try {
            return letterBagRepository.save(bag);
        } catch (Exception e) {
            System.err.println("LetterBag kaydedilirken hata: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Oyuncuya 7 rastgele harf ver
    public PlayerLetters assignInitialLetters(GameRoom room, User player, LetterBag bag) {
        Map<String, Integer> pool = bag.getRemainingLetters();
        List<String> hand = drawRandomLetters(pool, 7); // 7 harf çek
        bag.setRemainingLetters(pool); // Güncellenmiş havuz

        letterBagRepository.save(bag);

        PlayerLetters letters = new PlayerLetters();
        letters.setUser(player);
        letters.setRoom(room);
        letters.setLetters(hand);

        return playerLettersRepository.save(letters);
    }

    // Harf çekme algoritması (rastgele ama havuza göre)
    public List<String> drawRandomLetters(Map<String, Integer> pool, int count) {
        List<String> hand = new ArrayList<>();
        List<String> available = new ArrayList<>();

        pool.forEach((letter, quantity) -> {
            for (int i = 0; i < quantity; i++) {
                available.add(letter);
            }
        });

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            int index = random.nextInt(available.size());
            String chosen = available.get(index);
            hand.add(chosen);
            available.remove(index);

            // Harfi havuzdan eksilt
            pool.put(chosen, pool.get(chosen) - 1);
            if (pool.get(chosen) <= 0) pool.remove(chosen);
        }

        return hand;
    }

    public int getPointForLetter(String letter) {
        String normalized = letter.toUpperCase(new Locale("tr", "TR"));
        return letterPoolRepository.findByLetter(normalized)
                .map(LetterPool::getPoint)
                .orElse(0);
    }
}

