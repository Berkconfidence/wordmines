package com.example.wordmines.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // İstemcilere gidecek mesajlar için kullanılacak prefix
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user");

        // İstemciden gelen mesajlar için kullanılacak prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint'i tanımla ve CORS yapılandırması
        registry.addEndpoint("/ws")
                .withSockJS();
    }
}