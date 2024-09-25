package com.example.exodia.video.repository;

import com.example.exodia.video.domain.VideoParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoParticipantRepository extends JpaRepository<VideoParticipant, Long> {
    Optional<VideoParticipant> findByRoomRoomNameAndUserUserNum(String roomName, String userNum);
    int countByRoomRoomName(String roomName);
}
