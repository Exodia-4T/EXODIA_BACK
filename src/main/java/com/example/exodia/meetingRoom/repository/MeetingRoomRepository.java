package com.example.exodia.meetingRoom.repository;

import com.example.exodia.meetingRoom.domain.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {
    Optional<MeetingRoom> findByName(String name);
}
