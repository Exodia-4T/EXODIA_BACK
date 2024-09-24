package com.example.exodia.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDetailDto {

    private Long id;
    private String content;
    private String userNum;
    private String name;
    private LocalDateTime createdAt;
    private boolean isEdited;
}
