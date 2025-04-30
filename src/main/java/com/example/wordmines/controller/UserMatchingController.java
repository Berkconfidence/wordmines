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

import java.security.Principal;
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
    public void findMatch(@Payload GameOptions gameOptions, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String userId = gameOptions.getUserId();
        String principalName = principal.getName();

        // Kullanıcı bilgilerini WebSocket session'a kaydet
        headerAccessor.getSessionAttributes().put("userId", userId);

        try {
            // Eşleşme servisine kullanıcıyı ekle (Principal ile birlikte)
            MatchmakingRequest request = new MatchmakingRequest(userId, gameOptions.getDuration());
            matchmakingService.addToQueue(request, principalName);

            // Eşleşme güncellemesini gönder
            sendMatchmakingUpdate();

            // Eşleşme kontrolü yap
            checkAndCreateMatch(userId, principalName);
        } catch (Exception e) {
            // Hata durumunda bildir
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Eşleşme işlemi sırasında hata: " + e.getMessage());
            messagingTemplate.convertAndSendToUser(principalName, "/queue/matchmaking-error", errorResponse);
        }
    }

    // Eşleşme aramasını iptal etme
    @MessageMapping("/cancel-matchmaking")
    public void cancelMatchmaking(@Payload Map<String, String> payload, Principal principal) {
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
    private void checkAndCreateMatch(String userId, String principalName) {
        Map<String, String> matchInfo = matchmakingService.findMatch(userId);
        
        if (matchInfo != null) {
            String matchedUserId = matchInfo.get("userId");
            String matchedPrincipalName = matchInfo.get("principalName");

            Long roomId = System.currentTimeMillis();

            MatchmakingResponse response1;
            MatchmakingResponse response2;

            if(Integer.parseInt(userId)<Integer.parseInt(matchedUserId)) {
                // Her iki kullanıcıya da eşleşme bilgisini gönder
                response1 = new MatchmakingResponse(roomId, matchedUserId, false);
                response2 = new MatchmakingResponse(roomId, userId, true);
            }
            else {
                // Her iki kullanıcıya da eşleşme bilgisini gönder
                response1 = new MatchmakingResponse(roomId, matchedUserId, true);
                response2 = new MatchmakingResponse(roomId, userId, false);
            }


            System.out.println("Principal 1: " + principalName + ", Principal 2: " + matchedPrincipalName);
            try {
                // Principal isimlerini kullanarak mesaj gönder
                messagingTemplate.convertAndSendToUser(principalName, "/queue/game-matched", response1);
                messagingTemplate.convertAndSendToUser(matchedPrincipalName, "/queue/game-matched", response2);
            } catch (Exception e) {
                System.out.println("Mesaj gönderim hatası: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("userid:" + userId + " rakipid:" + matchedUserId);

            // Kullanıcıları kuyruktan çıkar
            matchmakingService.removeFromQueue(userId);
            matchmakingService.removeFromQueue(matchedUserId);

            // Güncelleme gönder
            sendMatchmakingUpdate();
        }
    }
}
