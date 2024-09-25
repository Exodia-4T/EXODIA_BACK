package com.example.exodia.common.controller;

import com.example.exodia.common.config.RedisMessagePublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

    private final RedisMessagePublisher messagePublisher;

    public WebSocketController(RedisMessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @MessageMapping("/room/join")
    @SendTo("/topic/rooms")
    public String joinRoom(@Payload String message, Principal principal) {
        messagePublisher.publish(principal.getName() + " joined the room");
        return principal.getName() + " joined";
    }

    @MessageMapping("/room/leave")
    @SendTo("/topic/rooms")
    public String leaveRoom(@Payload String message, Principal principal) {
        messagePublisher.publish(principal.getName() + " left the room");
        return principal.getName() + " left";
    }
}
