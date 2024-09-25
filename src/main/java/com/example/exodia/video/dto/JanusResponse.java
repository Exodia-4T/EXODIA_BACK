package com.example.exodia.video.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JanusResponse {
    private String janus;
    private Object data;
    private String transaction;
    private String session_id;
    private String handle_id;
}
