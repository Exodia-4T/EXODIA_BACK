package com.example.exodia.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RedisMessageSubscriber implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(RedisMessageSubscriber.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        logger.info("Received message: " + messageBody);
        // 방 생성, 입장, 퇴장 등의 이벤트에 따른 메시지 처리 로직
        // 예: 방 참가자 수 업데이트, 사용자 알림 전송 등
    }

}
