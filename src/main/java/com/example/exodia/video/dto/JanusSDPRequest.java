package com.example.exodia.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JanusSDPRequest {
    private String janus = "message";
    private Object body;
    private String sdp;

    public JanusSDPRequest(String sdp) {
        this.janus = "message";
        this.sdp = sdp;
    }
}
