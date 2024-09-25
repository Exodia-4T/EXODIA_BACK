package com.example.exodia.chat.service;

import com.example.exodia.chat.domain.ChatMessage;
import com.example.exodia.chat.domain.ChatRoom;
import com.example.exodia.chat.dto.ChatMessageRequest;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.chat.repository.ChatMessageRepository;
import com.example.exodia.chat.repository.ChatRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
public class ChatMessageService {
    private final RedisRepository redisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public ChatMessageService(RedisRepository redisRepository, @Qualifier("chat") RedisTemplate<String, Object> redisTemplate, ChannelTopic channelTopic, ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.redisRepository = redisRepository;
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    // 채팅방 입장
    public void enter(String userNum, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()->{return new EntityNotFoundException();});

        // 채팅방에 들어온 정보를 Redis 저장
        redisRepository.userEnterRoomInfo(userNum, roomId);
    }

    //채팅
    @Transactional
    public void sendMessage(ChatMessageRequest chatMessageRequest, User user) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessageRequest.getRoomId()).orElseThrow(()->{return new EntityNotFoundException();});

        //채팅 생성 및 저장 // 수정필요
        ChatMessage chatMessage = ChatMessage.builder()
                .message(chatMessageRequest.getMessage())
                .build();

        chatMessageRepository.save(chatMessage);
        String topic = channelTopic.getTopic();

        // ChatMessageRequest에 유저정보, 현재시간 저장
        chatMessageRequest.setName(user.getName());
        chatMessageRequest.setUserNum(user.getUserNum());

        redisTemplate.convertAndSend(topic, chatMessageRequest);
        redisTemplate.opsForHash();

    }


}
