package com.example.exodia.board.dto;

import com.example.exodia.board.domain.Board;
import com.example.exodia.board.domain.Category;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
public class BoardUpdateDto {

    private String title;
    private String content;
    private List<MultipartFile> files;
    private boolean isPinned;
    private Category category;
    private User user;
    @Builder.Default
    private DelYN delYn = DelYN.N;

    public Board updateFromEntity(Category category, User user) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .isPinned(this.isPinned)
                .category(category)
                .user(user)
                .delYn(this.delYn != null ? this.delYn : DelYN.N)
                .build();
    }
}
