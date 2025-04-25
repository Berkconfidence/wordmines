package com.example.wordmines.service;

import com.example.wordmines.model.MatchmakingRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchmakingService {

    private final Map<String, MatchmakingRequest> matchmakingQueue = new ConcurrentHashMap<>();
    private final Map<String, String> sessionMap = new ConcurrentHashMap<>(); // userId → sessionId eşlemesi

    public void addToQueue(MatchmakingRequest request, String sessionId) {
        matchmakingQueue.put(request.getUserId(), request);
        sessionMap.put(request.getUserId(), sessionId);
    }

    public void removeFromQueue(String userId) {
        matchmakingQueue.remove(userId);
        sessionMap.remove(userId);
    }

    public int getQueueLength() {
        return matchmakingQueue.size();
    }

    public String findMatch(String userId) {
        // Basit bir eşleştirme mantığı: Kuyrukta bekleyen ilk farklı kullanıcıyı bul
        for (String otherUserId : matchmakingQueue.keySet()) {
            if (!otherUserId.equals(userId)) {
                return otherUserId;
            }
        }
        return null;
    }

    public String getSessionId(String userId) {
        return sessionMap.get(userId);
    }
}