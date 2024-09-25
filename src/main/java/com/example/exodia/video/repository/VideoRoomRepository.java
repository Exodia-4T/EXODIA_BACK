package com.example.exodia.video.repository;

import com.example.exodia.video.domain.VideoRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface VideoRoomRepository extends JpaRepository<VideoRoom, Long> {
    Optional<VideoRoom> findByRoomNameAndIsActiveTrue(String roomName);
    void deleteByRoomName(String roomName);
    Optional<VideoRoom> findByRoomName(String roomName);
    List<VideoRoom> findAllByIsActiveTrue();
}
