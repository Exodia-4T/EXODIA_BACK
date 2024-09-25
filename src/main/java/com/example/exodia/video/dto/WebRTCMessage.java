package com.example.exodia.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WebRTCMessage {
    private String type;
    private String sdp;
    private String candidate;
    private String roomName;
}
