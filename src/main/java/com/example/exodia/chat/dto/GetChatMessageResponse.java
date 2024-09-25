package com.example.exodia.chat.dto;

import com.example.exodia.chat.domain.ChatMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetChatMessageResponse {

    private Long userId;
    private String name;
    private String message; // type이 image일 경우 객체 URL이 담김
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Boolean isFile;

    public GetChatMessageResponse(ChatMessage chatMessage) {
        this.userId = chatMessage.getChatUser().getUser().getId();
        this.name = chatMessage.getChatUser().getUser().getName();
        this.message = chatMessage.getMessage();
        this.createdAt = chatMessage.getCreatedAt();
    } // 수정 필요

    public GetChatMessageResponse(ChatMessageRequest request) {
        this.userId = request.getUserId();
        this.name = request.getName();
        this.message = request.getMessage();
        this.isFile = request.getIsFile();
        this.createdAt = LocalDateTime.now(); // 현재시간 저장
    }
}
