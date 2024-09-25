package com.example.exodia.chat.dto;

import com.example.exodia.chat.domain.ChatUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostChatRoomRequest {
    private String roomName;
    private List<ChatUser> anotherUsers;
}
