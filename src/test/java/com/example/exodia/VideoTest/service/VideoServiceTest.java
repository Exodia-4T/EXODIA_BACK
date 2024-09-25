package com.example.exodia.VideoTest.service;

import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.video.domain.VideoRoom;
import com.example.exodia.video.dto.CreateRoomDto;
import com.example.exodia.video.dto.JoinRoomDto;
import com.example.exodia.video.repository.VideoParticipantRepository;
import com.example.exodia.video.repository.VideoRoomRepository;
import com.example.exodia.video.service.VideoRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
public class VideoServiceTest {

    @Autowired
    private VideoRoomService videoRoomService;

    @MockBean
    private VideoRoomRepository videoRoomRepository;

    @MockBean
    private VideoParticipantRepository videoParticipantRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setup() throws Exception {
        User testUser = new User();
        Field userNumField = User.class.getDeclaredField("userNum");
        userNumField.setAccessible(true);
        userNumField.set(testUser, "testUser");

        Mockito.when(userRepository.findByUserNum(anyString())).thenReturn(Optional.of(testUser));
        Mockito.when(videoRoomRepository.findByRoomName(anyString())).thenReturn(Optional.of(new VideoRoom()));
    }

    @Test
    public void testCreateRoom() {
        CreateRoomDto createRoomDto = new CreateRoomDto("TestRoom", "1234");
        assertDoesNotThrow(() -> videoRoomService.createRoom(createRoomDto, "20240901001"));
    }

    @Test
    public void testJoinRoom() {
        JoinRoomDto joinRoomDto = new JoinRoomDto("TestRoom", "1234");
        assertDoesNotThrow(() -> videoRoomService.joinRoom(joinRoomDto, "20240901001"));
    }

    @Test
    public void testLeaveRoom() {
        assertDoesNotThrow(() -> videoRoomService.leaveRoom("TestRoom", "20240901001"));
    }
}
