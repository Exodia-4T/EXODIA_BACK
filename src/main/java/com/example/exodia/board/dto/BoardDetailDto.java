package com.example.exodia.board.dto;


import com.example.exodia.board.domain.Category;
import com.example.exodia.comment.dto.CommentResDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BoardDetailDto {

    private Long id;
    private String title;
    private String content;
    private Category category;
    private int hits;
    private String user_num;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> filePaths;
    private List<CommentResDto> comments;
}
