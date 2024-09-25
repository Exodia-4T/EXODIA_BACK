package com.example.exodia.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
//@AllArgsConstructor
@NoArgsConstructor
public class GetChatRoomResponse {

    private String roomName;
    private String chatRoomId;
    private String lastMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    private String dayBefore;

    public GetChatRoomResponse(
            String roomName,
            String chatRoomId,
            String lastMessage,
            LocalDateTime createAt,
            String dayBefore
    ) {
        this.roomName = roomName;
        this.chatRoomId = chatRoomId;
        this.lastMessage = lastMessage;
        this.createAt = createAt;
        this.dayBefore = dayBefore;
    }
}
