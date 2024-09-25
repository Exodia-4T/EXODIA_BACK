package com.example.exodia.board.repository;

import com.example.exodia.board.domain.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {
    void deleteByBoardId(Long boardId);
    List<BoardFile> findByBoardId(Long boardId);


}
