package com.ideality.coreflow.security.config;

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
        // SimpleBroker를 설정하여 클라이언트에게 메시지를 전달할 수 있는 경로를 설정
        config.enableSimpleBroker("/topic/notifications");  // /topic 경로로 메시지를 보낼 수 있도록 설정
        config.setApplicationDestinationPrefixes("/app");  // 클라이언트에서 보내는 요청을 처리할 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws 엔드포인트로 클라이언트가 WebSocket 연결을 시도
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173")  // 프론트엔드 주소 (CORS 설정)
                .withSockJS();  // SockJS를 사용하여 WebSocket을 지원하지 않는 브라우저를 폴백하도록 설정
    }
}

