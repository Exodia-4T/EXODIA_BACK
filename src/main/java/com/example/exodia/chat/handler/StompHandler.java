package com.example.exodia.chat.handler;

import com.example.exodia.chat.service.RedisRepository;
import com.example.exodia.common.auth.JwtTokenProvider;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;

    public StompHandler(JwtTokenProvider jwtTokenProvider, RedisRepository redisRepository, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisRepository = redisRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = "";
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
//            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("Authorization"));
            String jwt = accessor.getFirstNativeHeader("Authorization");
            if (StringUtils.hasText(jwt) && jwt.startsWith("Bearer")) {
                jwtToken = Objects.requireNonNull(accessor.getFirstNativeHeader("token")).substring(7);

                String userNum = jwtTokenProvider.getUserNumFromToken(jwtToken);

                User user = userRepository.findByUserNumAndDelYn(userNum, DelYN.valueOf("N")).orElseThrow(() -> {
                    return new EntityNotFoundException("user가 없습니다.");
                });

                String sessionId = (String) message.getHeaders().get("simpSessionId");
                redisRepository.saveMyInfo(sessionId, userNum);
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 채팅방에서 나가는 것이 맞는지 확인
            if (redisRepository.existMyInfo(sessionId)) {
                String userNum = redisRepository.getMyInfo(sessionId);

                // 채팅방 퇴장 정보 저장
                if (redisRepository.existChatRoomUserInfo(userNum)) {
                    redisRepository.exitUserEnterRoomId(userNum);
                }

                redisRepository.deleteMyInfo(sessionId);
            }
        }
        return message;
    }
}