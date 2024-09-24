package com.example.exodia.board.dto;

import com.example.exodia.board.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardListResDto {

    private Long id;
    private String title;
    private Category category;
    private int hits;
    private String user_num;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPinned;
}
