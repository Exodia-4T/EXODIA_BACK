package com.example.exodia.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPinReqDto {
    private Long userId;
    private Boolean isPinned;
}
