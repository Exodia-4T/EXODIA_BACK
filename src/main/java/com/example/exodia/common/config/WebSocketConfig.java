package com.example.exodia.common.config;

import com.example.exodia.chat.handler.StompHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 문자 채팅용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    public WebSocketConfig(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // topic, queue 메시지를 구독하는 요청 url => 즉 메시지 받을 때
        config.setApplicationDestinationPrefixes("/pub"); // app 메시지를 발행하는 요청 url => 즉 메시지 보낼 때 // // @MessageMapping("hello") 라면 경로는 -> /pub/hello
    }

    // 웹 소켓 연결을 위한 엔드포인트 설정 및 stomp sub/pub 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")   // stomp 접속 주소 url , 연결될 엔드포인트 ws://localhost:8080/stomp/chat
                .setAllowedOrigins("*")
                .withSockJS(); ; // SocketJS 를 연결
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}

