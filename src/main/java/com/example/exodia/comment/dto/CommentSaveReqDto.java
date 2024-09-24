package com.example.exodia.comment.dto;


import com.example.exodia.board.domain.Board;
import com.example.exodia.comment.domain.Comment;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentSaveReqDto {

    private Long board_id;
    private String userNum;
    private String content;
    private String name;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 기본값을 현재 시간으로 설정

    @Builder.Default
    private DelYN delYn = DelYN.N; // 기본값을 N으로 설정

    public Comment BoardToEntity(User user, Board board, String userNum) {
        return Comment.builder()
                .content(this.content)
                .delYn(this.delYn)
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now())
                .user(user)
                .name(this.name)
                .userNum(userNum)
                .board(board)
                .build();
    }
}

