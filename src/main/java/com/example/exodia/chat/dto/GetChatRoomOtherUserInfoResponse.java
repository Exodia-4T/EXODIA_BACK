package com.example.exodia.chat.dto;

import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRoomOtherUserInfoResponse { // 다른 유저 정보 조회

    private Long otherUserId;
    private String otherUserNickname;

    public GetChatRoomOtherUserInfoResponse(User otherUser) {
        this.otherUserId = otherUser.getId();
        this.otherUserNickname = otherUser.getName();
    }
}
