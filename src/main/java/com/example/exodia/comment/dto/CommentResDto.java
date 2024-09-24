package com.example.exodia.comment.dto;

import com.example.exodia.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResDto {
    private Long id;
    private String content;
    private String userNum;
    private String createdAt;
    private String name;


    public static CommentResDto fromEntity(Comment comment) {
        return CommentResDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userNum(comment.getUserNum())
                .name(comment.getName())
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }
}