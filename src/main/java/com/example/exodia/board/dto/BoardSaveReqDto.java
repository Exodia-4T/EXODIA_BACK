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
public class BoardSaveReqDto {

    private String title;
    private String content;
    private Category category;
    private String userNum;
    private List<MultipartFile> files;
    private boolean isPinned;
    private boolean isAnonymous;

    @Builder.Default
    private DelYN delYn = DelYN.N;

    // 익명 게시글 여부에 따라 User를 null로 설정
    public Board toEntity(User user, Category category) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .category(category)
                .delYn(this.delYn != null ? this.delYn : DelYN.N)
                .user(this.isAnonymous ? null : user)  // 익명일 경우 user를 null로 설정
                .isPinned(this.isPinned)
                .isAnonymous(this.isAnonymous)  // 익명 여부 설정
                .build();
    }
}
