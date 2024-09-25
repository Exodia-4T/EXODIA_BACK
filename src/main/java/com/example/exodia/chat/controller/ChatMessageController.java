package com.example.exodia.chat.controller;

import com.example.exodia.chat.dto.ChatMessageRequest;
import com.example.exodia.chat.service.ChatMessageService;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

@RestController
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    public ChatMessageController(ChatMessageService chatMessageService, UserRepository userRepository) {
        this.chatMessageService = chatMessageService;
        this.userRepository = userRepository;
    }


     // websocket "/pub/chat/enter"로 들어오는 메시징을 처리
     // 채팅방에 입장했을 경우
    @MessageMapping("/chat/enter")
    public void enter(ChatMessageRequest chatMessageRequest) { // 수정필요
        User user = userRepository.findByUserNumAndDelYn(chatMessageRequest.getUserNum(), DelYN.N).orElseThrow(()->{return new EntityNotFoundException("없어용");});
        chatMessageService.enter(user.getUserNum(), chatMessageRequest.getRoomId());
    }

     // websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
    @MessageMapping("/chat/message")
    public void message(ChatMessageRequest chatMessageRequest) {
        User user = userRepository.findByUserNumAndDelYn(chatMessageRequest.getUserNum(), DelYN.N).orElseThrow(()->{return new EntityNotFoundException("없어용");});
        chatMessageService.sendMessage(chatMessageRequest, user);
    }
}
