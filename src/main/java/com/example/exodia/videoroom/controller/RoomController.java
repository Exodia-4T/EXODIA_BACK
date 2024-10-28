package com.example.exodia.videoroom.controller;

import com.example.exodia.videoroom.domain.Participant;
import com.example.exodia.videoroom.domain.Room;
import com.example.exodia.videoroom.dto.RoomRequestDto;
import com.example.exodia.videoroom.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // RoomRequestDto로 roomName과 password를 JSON으로 받음
    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequestDto roomRequestDto) {
        Room room = roomService.createRoom(roomRequestDto.getRoomName(), roomRequestDto.getPassword());
        return ResponseEntity.ok(room);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Room>> getRoomList() {
        List<Room> rooms = roomService.getRoomList();
        return ResponseEntity.ok(rooms);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{roomId}/participants")
    public ResponseEntity<List<Participant>> getParticipants(@PathVariable Long roomId) {
        List<Participant> participants = roomService.getParticipants(roomId);
        return ResponseEntity.ok(participants);
    }
}