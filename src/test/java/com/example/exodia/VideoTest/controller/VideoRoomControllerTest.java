package com.example.exodia.VideoTest.controller;

import com.example.exodia.common.auth.JwtTokenProvider;
import com.example.exodia.video.controller.VideoRoomController;
import com.example.exodia.video.dto.CreateRoomDto;
import com.example.exodia.video.dto.JoinRoomDto;
import com.example.exodia.video.service.VideoRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoRoomController.class)
public class VideoRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoRoomService videoRoomService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private String token;

    @BeforeEach
    public void setup() {
        token = "Bearer test-token";
        Mockito.when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        Mockito.when(jwtTokenProvider.getUserNumFromToken(anyString())).thenReturn("testUser");
    }

    @Test
    public void testCreateRoom() throws Exception {
        CreateRoomDto createRoomDto = new CreateRoomDto("TestRoom", "1234");

        ResultActions result = mockMvc.perform(post("/api/video/create")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roomName\":\"TestRoom\",\"password\":\"1234\"}"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.status_message").value("Room created successfully"));
    }

    @Test
    public void testJoinRoom() throws Exception {
        JoinRoomDto joinRoomDto = new JoinRoomDto("TestRoom", "1234");

        ResultActions result = mockMvc.perform(post("/api/video/join")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"roomName\":\"TestRoom\",\"password\":\"1234\"}"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.status_message").value("Joined room successfully"));
    }

    @Test
    public void testLeaveRoom() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/video/leave")
                .header("Authorization", token)
                .param("roomName", "TestRoom"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.status_message").value("Left room successfully"));
    }
}
