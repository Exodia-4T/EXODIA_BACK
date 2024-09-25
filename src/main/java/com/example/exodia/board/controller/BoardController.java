package com.example.exodia.board.controller;

import com.example.exodia.board.dto.*;
import com.example.exodia.board.service.BoardService;
import com.example.exodia.common.dto.CommonErrorDto;
import com.example.exodia.common.dto.CommonResDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/create")
    public String 게시물작성화면() {
        return "/board/create";
    }

    // 게시물을 작성합니다.
    @PostMapping("/create")
    public ResponseEntity<?> 게시물작성(@ModelAttribute BoardSaveReqDto dto) {
        try {
            boardService.createBoard(dto, dto.getFiles());
            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "게시물이 성공적으로 등록되었습니다.", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (SecurityException | EntityNotFoundException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 게시물 목록을 조회합니다.
    @GetMapping("/list")
    public ResponseEntity<?> 게시물목록(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "searchQuery", required = false) String searchQuery,
            @RequestParam(value = "searchType", required = false) String searchType) {

        Page<BoardListResDto> boardListResDto = boardService.BoardListWithSearch(pageable, searchType, searchQuery);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "게시물 목록을 반환합니다.", boardListResDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 게시물 상세 정보를 조회합니다.
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> 게시물상세조회(@PathVariable Long id) {
        try {
            BoardDetailDto boardDetail = boardService.BoardDetail(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "게시물 상세 정보를 반환합니다.", boardDetail);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.NOT_FOUND, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }

    // 게시물을 수정합니다.
    @PostMapping("/update/{id}")
    public ResponseEntity<?> 게시물수정(@PathVariable Long id, @ModelAttribute BoardUpdateDto dto) {
        try {
            boardService.updateBoard(id, dto, dto.getFiles());
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "게시물이 성공적으로 수정되었습니다.", id);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.NOT_FOUND, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }

    // 게시물을 삭제합니다.
    @GetMapping("/delete/{id}")
    public ResponseEntity<?> 게시물삭제(@PathVariable Long id) {
        try {
            boardService.deleteBoard(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "게시물이 성공적으로 삭제되었습니다.", id);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.NOT_FOUND, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }

    // 게시물 상단 고정 기능 추가
    @PostMapping("/pin/{id}")
    public ResponseEntity<?> 게시물상단고정(@PathVariable Long id, @RequestBody BoardPinReqDto requestDto) {

        try {
            boardService.pinBoard(id, requestDto.getUserId(), requestDto.getIsPinned());

            String message = requestDto.getIsPinned()
                    ? "게시물이 상단에 고정되었습니다."
                    : "게시물의 상단 고정이 해제되었습니다.";

            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, message, id);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);

        } catch (SecurityException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.FORBIDDEN, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.FORBIDDEN);

        } catch (IllegalArgumentException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);

        } catch (EntityNotFoundException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.NOT_FOUND, e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }




}
