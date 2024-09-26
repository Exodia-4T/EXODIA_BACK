package com.example.exodia.meetingRoom.service;

import com.example.exodia.meetingRoom.domain.MeetingRoom;
import com.example.exodia.meetingRoom.repository.MeetingRoomRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingRoomService {

    @Autowired
    private MeetingRoomRepository meetingRoomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    /* 회의실 추가 */
    public MeetingRoom createMeetRoom(String name) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        userService.checkHrAuthority(user.getDepartment().getId().toString());

        if (meetingRoomRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 회의실 이름입니다.");
        }
        MeetingRoom meetingRoom = MeetingRoom.builder()
                .name(name)
                .build();
        return meetingRoomRepository.save(meetingRoom);
    }

    /* 회의실 목록 조회 */
    public List<MeetingRoom> listMeetRoom() {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        return meetingRoomRepository.findAll();
    }

    /* 회의실 이름 변경 */
    public MeetingRoom updateMeetRoom(Long id, String newName) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        userService.checkHrAuthority(user.getDepartment().getId().toString());
        MeetingRoom meetingRoom = meetingRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회의실을 찾을 수 없습니다."));
        meetingRoom.setName(newName);
        return meetingRoomRepository.save(meetingRoom);
    }

    /* 회의실 삭제 */
    public void deleteMeetRoom(Long id) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        userService.checkHrAuthority(user.getDepartment().getId().toString());
        if (!meetingRoomRepository.existsById(id)) {
            throw new IllegalArgumentException("회의실을 찾을 수 없습니다.");
        }
        meetingRoomRepository.deleteById(id);
    }
}

