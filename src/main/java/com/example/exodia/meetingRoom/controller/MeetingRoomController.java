package com.example.exodia.meetingRoom.controller;

import com.example.exodia.common.dto.CommonResDto;
import com.example.exodia.meetingRoom.domain.MeetingRoom;
import com.example.exodia.meetingRoom.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting-room")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    /* 회의실 삭제 */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResDto> addMeetingRoom(@RequestParam String name) {
        MeetingRoom meetingRoom = meetingRoomService.createMeetRoom(name);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "회의실이 추가되었습니다.", meetingRoom), HttpStatus.CREATED);
    }

    /* 회의실 삭제 */
    @GetMapping("/list")
    public ResponseEntity<List<MeetingRoom>> getAllMeetingRooms() {
        List<MeetingRoom> meetingRooms = meetingRoomService.listMeetRoom();
        return ResponseEntity.ok(meetingRooms);
    }

    /* 회의실 이름변경 */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResDto> updateMeetingRoom(@PathVariable Long id, @RequestParam String newName) {
        MeetingRoom updatedMeetingRoom = meetingRoomService.updateMeetRoom(id, newName);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "회의실 이름이 변경되었습니다.", updatedMeetingRoom), HttpStatus.OK);
    }

    /* 회의실 삭제 */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResDto> deleteMeetingRoom(@PathVariable Long id) {
        meetingRoomService.deleteMeetRoom(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "회의실이 삭제되었습니다.", null), HttpStatus.OK);
    }
}
