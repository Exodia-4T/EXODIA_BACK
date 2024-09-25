package com.example.exodia.common.config;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic roomEventsTopic;

    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic roomEventsTopic) {
        this.redisTemplate = redisTemplate;
        this.roomEventsTopic = roomEventsTopic;
    }

    public void publish(String message) {
        redisTemplate.convertAndSend(roomEventsTopic.getTopic(), message);
    }
}
