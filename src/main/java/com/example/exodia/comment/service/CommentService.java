package com.example.exodia.comment.service;

import com.example.exodia.board.domain.Board;
import com.example.exodia.board.repository.BoardRepository;
import com.example.exodia.comment.domain.Comment;
import com.example.exodia.comment.dto.CommentDetailDto;
import com.example.exodia.comment.dto.CommentSaveReqDto;
import com.example.exodia.comment.repository.CommentRepository;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    @Autowired
    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public Comment saveComment(CommentSaveReqDto dto) {

        User user = userRepository.findByUserNum(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다람."));

        String userNum = user.getUserNum();

        if (dto.getDelYn() == null) {
            dto.setDelYn(DelYN.N); // delYn 값이 없을 경우 N으로 설정
        }

        Comment savedComment;
        if (dto.getBoard_id() != null) {
            Board board = boardRepository.findById(dto.getBoard_id())
                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

            savedComment = commentRepository.save(dto.BoardToEntity(user, board, userNum));
        } else {
            throw new IllegalArgumentException("댓글이 달릴 게시글 필요합니다.");
        }

        return savedComment;
    }



    // 게시글의 댓글 리스트
    public List<CommentDetailDto> getCommentsByBoardId(Long BoardId) {
        List<Comment> comments = commentRepository.findByBoardIdAndDelYn(BoardId, DelYN.N);
        return comments.stream()
                .map(comment -> CommentDetailDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .userNum(comment.getUser().getUserNum())
                        .name(comment.getUser().getName())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    public Comment commentDelete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));
        comment.updateDelYN(DelYN.Y);
        return comment;
    }
}
