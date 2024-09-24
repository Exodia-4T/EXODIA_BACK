package com.example.exodia.board.repository;

import com.example.exodia.board.domain.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {

    // 특정 board_id로 파일을 삭제하는 메서드
    void deleteByBoardId(Long boardId);

    List<BoardFile> findByBoardId(Long boardId);


}
