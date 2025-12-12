package com.example.ny.Config;

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
        config.enableSimpleBroker("/topic"); // Nơi gửi dữ liệu về cho người chơi
        config.setApplicationDestinationPrefixes("/app"); // Nơi nhận dữ liệu từ người chơi
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là cái cổng để Frontend kết nối vào
        registry.addEndpoint("/ws-game").withSockJS();
    }
}