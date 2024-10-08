package com.example.exodia.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentUpdateDto {
    private String content;
    private LocalDateTime updatedAt;
    private String userNum;
}
