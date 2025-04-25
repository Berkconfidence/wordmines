package com.example.wordmines.controller;

import com.example.wordmines.model.GameOptions;

import com.example.wordmines.model.MatchmakingRequest;
import com.example.wordmines.model.MatchmakingResponse;
import com.example.wordmines.model.MatchmakingUpdate;
import com.example.wordmines.service.MatchmakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserMatchingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MatchmakingService matchmakingService;

    // Eşleşme araması başlatma
    @MessageMapping("/find-match")
    public void findMatch(@Payload GameOptions gameOptions, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String userId = gameOptions.getUserId();

        // Kullanıcı bilgilerini WebSocket session'a kaydet
        headerAccessor.getSessionAttributes().put("userId", userId);

        try {
            // Eşleşme servisine kullanıcıyı ekle
            MatchmakingRequest request = new MatchmakingRequest(userId, gameOptions.getDuration());
            matchmakingService.addToQueue(request, sessionId);

            // Eşleşme güncellemesini gönder
            sendMatchmakingUpdate();

            // Eşleşme kontrolü yap
            checkAndCreateMatch(userId);
        } catch (Exception e) {
            // Hata durumunda bildir
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Eşleşme işlemi sırasında hata: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(userId, "/queue/matchmaking-error", errorResponse);
        }
    }

    // Eşleşme aramasını iptal etme
    @MessageMapping("/cancel-matchmaking")
    public void cancelMatchmaking(@Payload Map<String, String> payload) {
        String userId = payload.get("userId");

        // Kullanıcıyı eşleşme kuyruğundan çıkar
        matchmakingService.removeFromQueue(userId);

        // Eşleşme güncellemesini gönder
        sendMatchmakingUpdate();
    }

    // Eşleşme güncellemelerini gönderme
    private void sendMatchmakingUpdate() {
        int queueLength = matchmakingService.getQueueLength();
        int estimatedTime = queueLength * 5; // Örnek hesaplama

        MatchmakingUpdate update = new MatchmakingUpdate(queueLength, estimatedTime);
        messagingTemplate.convertAndSend("/topic/matchmaking-update", update);
    }

    // Eşleşme kontrolü ve oda oluşturma
    private void checkAndCreateMatch(String userId) {
        String matchedUserId = matchmakingService.findMatch(userId);

        if (matchedUserId != null) {
            // Eşleşme bulundu, oda oluştur
            String roomId = "room_" + System.currentTimeMillis();

            // Her iki kullanıcıya da eşleşme bilgisini gönder
            MatchmakingResponse response1 = new MatchmakingResponse(roomId, matchedUserId);
            MatchmakingResponse response2 = new MatchmakingResponse(roomId, userId);

            // Session ID'leri al
            String session1 = matchmakingService.getSessionId(userId);
            String session2 = matchmakingService.getSessionId(matchedUserId);

            System.out.println(session1+" "+session2);
            try {
                messagingTemplate.convertAndSendToUser(userId, "/queue/game-matched", response1);
                messagingTemplate.convertAndSendToUser(matchedUserId, "/queue/game-matched", response2);
            } catch (Exception e) {
                System.out.println("Mesaj gönderim hatası: " + e.getMessage());
            }
            System.out.println("userid:"+userId+"rakipid:"+matchedUserId+" "+roomId);

            // Kullanıcıları kuyruktan çıkar
            matchmakingService.removeFromQueue(userId);
            matchmakingService.removeFromQueue(matchedUserId);

            // Güncelleme gönder
            sendMatchmakingUpdate();
        }
    }
}