package com.example.exodia.chat.domain;

import com.example.exodia.common.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "del_yn = 'N'")
public class ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 채팅메세지고유의 id

    //    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom; // 어느 채팅방인지

    //    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ManyToOne
    @JoinColumn(name = "chat_user_id", nullable = false)
    private ChatUser chatUser; // 그 방의 누가 보냈는지 (이 안에 채팅방번호가 있는데 채팅방은 없어도 되지 않을까..)

    private String content;
}
