package com.example.exodia.chat.controller;

import com.example.exodia.chat.dto.GetChatRoomResponse;
import com.example.exodia.chat.dto.PostChatRoomRequest;
import com.example.exodia.common.dto.CommonResDto;
import com.example.exodia.chat.service.ChatRoomService;
import com.example.exodia.chat.service.RedisRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final RedisRepository redisRepository;

    public ChatRoomController(ChatRoomService chatRoomService, RedisRepository redisRepository) {
        this.chatRoomService = chatRoomService;
        this.redisRepository = redisRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getChatRooms(Pageable pageable) { // 수정필요
        Page<GetChatRoomResponse> chatRoomList = chatRoomService.getChatRoomsList(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅목록조회성공", chatRoomList);
        return new ResponseEntity<>(chatRoomList,HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(@RequestBody PostChatRoomRequest dto){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "채팅방생성성공", chatRoomService.createChatRoom(dto));
        return new ResponseEntity<>(commonResDto,HttpStatus.OK);
    }

    // 메세지 불러오기
    // 채팅방나가기
    // 채팅방 아예 나가기
    // 채팅방 삭제하기

}
