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
public class ChatFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 채팅파일고유의 id

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom; // 어느 채팅방인지

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ManyToOne
    @JoinColumn(name = "chat_user_id", nullable = false)
    private ChatUser chatUser; // 그 방의 누가 보냈는지 (이 안에 채팅방번호가 있는데 채팅방은 없어도 되지 않을까..)

    @OneToOne
    @JoinColumn(name = "chat_message_id", nullable = false)
    private ChatMessage chatMessage; // 파일을 보내는 순간 먼저 생겨서 참조되어야한다. 메세지 내용에 채팅파일id를 넣어야 할듯..?(메세지 순서를 확인하기 위해서)

    private String chatFileName;

    private String chatFileUrl;
}
