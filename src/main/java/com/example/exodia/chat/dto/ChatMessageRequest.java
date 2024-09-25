package com.example.exodia.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest implements Serializable {

    public enum MessageType {
        ENTER, TALK, GROUP_TALK, IMAGE, FILE, LEAVE, QUIT;
    }

    private Long messageId;
    private String roomName;
    private MessageType type; // 메시지 타입
    private String name;
    private Long roomId;
//    private Set<Long> otherUserIds;
    private String message; // 메시지
    private String userNum;
//    private int comunt;
    private Boolean isFile;
}
