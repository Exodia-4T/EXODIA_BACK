package com.example.exodia.board.repository;

import com.example.exodia.board.domain.Board;
import com.example.exodia.common.domain.DelYN;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findAll(Pageable pageable);


    // 제목을 기준으로 게시물을 검색하고 삭제되지 않은 항목만 반환
    Page<Board> findByTitleContainingIgnoreCase(String title, DelYN delYN, Pageable pageable);

    // 내용을 기준으로 게시물을 검색하고 삭제되지 않은 항목만 반환
    Page<Board> findByContentContainingIgnoreCase(String content, DelYN delYN, Pageable pageable);

    // 제목 또는 내용을 기준으로 게시물을 검색하고 삭제되지 않은 항목만 반환
    Page<Board> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content, DelYN delYN, Pageable pageable);

    // 작성자 사번(userNum)을 기준으로 게시물을 검색하고 삭제되지 않은 항목만 반환
    Page<Board> findByUser_UserNumAndDelYn(String userNum, DelYN delYN, Pageable pageable);

    // 작성자 이름(name)을 기준으로 게시물을 검색하고 삭제되지 않은 항목만 반환
    Page<Board> findByUser_NameContainingIgnoreCase(String name, DelYN delYN, Pageable pageable);

    // 상단 고정된 게시물을 상단에 위치시키고, 그 외 게시물은 최신순으로 정렬
    @Query("SELECT b FROM Board b WHERE b.delYn = 'N' ORDER BY b.isPinned DESC, b.createdAt DESC")
    Page<Board> findAllWithPinned(Pageable pageable);

    Optional<Board> findById(Long boardId);
}
