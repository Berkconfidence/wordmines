package com.example.wordmines.wordvalidation;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class DictionaryLoader {

    // Bu sete tüm kelimeler yüklenecek
    private final Set<String> validWords = new HashSet<>();

    // Uygulama başlarken bir kez çağrılır
    @PostConstruct
    public void loadWords() {
        // Kelime listesi klasörünün yolu
        Path wordListDir = Paths.get("src", "main", "java", "com", "example", "wordmines", "wordlist");

        try (Stream<Path> files = Files.list(wordListDir)) {
            files.filter(path -> path.toString().endsWith(".list"))
                    .forEach(path -> {
                        try (Stream<String> lines = Files.lines(path)) {
                            lines.map(String::toLowerCase)
                                    .map(String::trim)
                                    .filter(line -> !line.isEmpty())
                                    .forEach(validWords::add);
                        } catch (IOException e) {
                            System.err.println("Dosya okunurken hata: " + path);
                            e.printStackTrace();
                        }
                    });

            System.out.println("Toplam kelime yüklendi: " + validWords.size());

        } catch (IOException e) {
            System.err.println("Kelime dosyaları yüklenemedi");
            e.printStackTrace();
        }
    }

    // Harici kullanımlar için kelime doğrulama metodu
    public boolean isValidWord(String word) {
        return validWords.contains(word.toLowerCase().trim());
    }
}
