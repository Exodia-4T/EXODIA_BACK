package com.example.exodia.comment.repository;

import com.example.exodia.comment.domain.Comment;
import com.example.exodia.common.domain.DelYN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 게시물 ID로 댓글 조회
    List<Comment> findByBoardId(Long boardId);

    // 게시물 ID와 DelYN으로 댓글 조회
    List<Comment> findByBoardIdAndDelYn(Long boardId, DelYN delYn);
}
