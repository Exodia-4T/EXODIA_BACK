package com.example.exodia.board.dto;

import com.example.exodia.board.domain.Board;
import com.example.exodia.board.domain.Category;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardSaveReqDto {

    private String title;
    private String content;
    private Category category;
    private String userNum;
    private List<MultipartFile> files;
    private boolean isPinned;
    private Long hits = 0L;

    @Builder.Default
    private DelYN delYn = DelYN.N;


    public Board toEntity(User user, Category category) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .category(category)
                .delYn(this.delYn != null ? this.delYn : DelYN.N)
                .isPinned(this.isPinned)
                .user(user)
                .hits(this.hits)
                .build();
    }
}