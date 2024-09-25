package com.example.exodia.chat.service;

import com.example.exodia.chat.domain.ChatRoom;
import com.example.exodia.chat.domain.ChatUser;
import com.example.exodia.chat.dto.GetChatRoomResponse;
import com.example.exodia.chat.dto.PostChatRoomRequest;
import com.example.exodia.chat.repository.ChatRoomRepository;
import com.example.exodia.chat.repository.ChatUserRepository;
import com.example.exodia.chat.repository.ChatMessageRepository;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RedisRepository redisRepository;

    public ChatRoomService(UserRepository userRepository, ChatRoomRepository chatRoomRepository, ChatUserRepository chatRoomUserRepository, ChatMessageRepository chatMessageRepository, RedisRepository redisRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomUserRepository = chatRoomUserRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.redisRepository = redisRepository;
    }

    @Transactional
    public Long createChatRoom(PostChatRoomRequest req) {
        // 상대방 존재 체크 // 수정필요
        List<User> anotherUsers = new ArrayList<>();
        for(ChatUser u : req.getAnotherUsers()){
            anotherUsers.add(userRepository.findByUserNumAndDelYn(u.getUser().getUserNum(), DelYN.valueOf("N")).orElseThrow(()->{return new EntityNotFoundException();}));
        }

        // 방 존재 확인
        if(existRoom(anotherUsers)) { // 수정필요
            ChatRoom savedChatRoom = chatRoomRepository.findByRoomNameAndDelYn(req.getRoomName(),DelYN.valueOf("N")).orElseThrow(()->{return new EntityNotFoundException();});
            return savedChatRoom.getId();
        }

        // 존재하는 방 없다면 생성
        ChatRoom room = ChatRoom.builder().build();
        chatRoomRepository.save(room);

        // 채팅 매핑 데이터 생성  // 수정필요
        ChatUser chatRoomUser = new ChatUser(); // 만든이
        // 상대방 유저들 채팅 매핑 데이터 생성
        ChatUser chatRoomAnotherUsers = new ChatUser();

        chatRoomUserRepository.save(chatRoomUser);
        chatRoomUserRepository.save(chatRoomAnotherUsers);

        return room.getId();
    }

    private boolean existRoom(List<User> users) { // 수정필요
        return false;
    }

    public Page<GetChatRoomResponse> getChatRoomsList(Pageable pageable) { // 수정필요
        Page<ChatRoom> chatRooms = chatRoomRepository.findAll(pageable); // 수정필요
        return chatRooms.map(ChatRoom::dtoFromEntity);
    }


}
