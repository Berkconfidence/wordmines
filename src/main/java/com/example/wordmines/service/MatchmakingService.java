package com.example.wordmines.service;

import com.example.wordmines.model.MatchmakingRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MatchmakingService {

    private final Map<String, MatchmakingRequest> matchmakingQueue = new ConcurrentHashMap<>();
    private final Map<String, String> userToPrincipalMap = new ConcurrentHashMap<>(); // userId → principalName
    private final Map<String, String> principalToUserMap = new ConcurrentHashMap<>(); // principalName → userId

    public void addToQueue(MatchmakingRequest request, String principalName) {
        String userId = request.getUserId();
        matchmakingQueue.put(userId, request);
        userToPrincipalMap.put(userId, principalName);
        principalToUserMap.put(principalName, userId);
    }

    public void removeFromQueue(String userId) {
        String principalName = userToPrincipalMap.get(userId);
        matchmakingQueue.remove(userId);
        userToPrincipalMap.remove(userId);
        if (principalName != null) {
            principalToUserMap.remove(principalName);
        }
    }

    public int getQueueLength() {
        return matchmakingQueue.size();
    }

    public Map<String, String> findMatch(String userId) {
        // Basit bir eşleştirme mantığı: Kuyrukta bekleyen ilk farklı kullanıcıyı bul
        for (String otherUserId : matchmakingQueue.keySet()) {
            if (!otherUserId.equals(userId)) {
                Map<String, String> result = new HashMap<>();
                result.put("userId", otherUserId);
                result.put("principalName", userToPrincipalMap.get(otherUserId));
                return result;
            }
        }
        return null;
    }

    public String getPrincipalName(String userId) {
        return userToPrincipalMap.get(userId);
    }

    public String getUserId(String principalName) {
        return principalToUserMap.get(principalName);
    }
}
