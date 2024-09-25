package com.example.exodia.chat.domain;

import com.example.exodia.chat.dto.GetChatRoomResponse;
import com.example.exodia.common.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "del_yn = 'N'")
public class ChatRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 채팅방 고유의 id

    @Column(nullable = false)
    private String roomName;

//    private LocalDateTime recentTime; // 채팅방 목록을 가져올 때 최근 순으로 가져오기 위함.

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatUser> chatUsers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public GetChatRoomResponse dtoFromEntity(){
        return new GetChatRoomResponse();
    }
}
