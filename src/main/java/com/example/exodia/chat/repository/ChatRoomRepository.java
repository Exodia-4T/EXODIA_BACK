package com.example.exodia.chat.repository;

import com.example.exodia.chat.domain.ChatRoom;
import com.example.exodia.common.domain.DelYN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomNameAndDelYn(String roomName, DelYN delYn);
}
