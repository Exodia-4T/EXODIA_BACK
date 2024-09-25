package com.example.exodia.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JanusCandidateRequest {
    private String janus = "trickle";
    private Object candidate;

    public JanusCandidateRequest(String candidate) {
        this.janus = "trickle";
        this.candidate = candidate;
    }
}
