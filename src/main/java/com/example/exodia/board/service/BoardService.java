package com.example.exodia.board.service;

import com.example.exodia.board.domain.Board;
import com.example.exodia.board.domain.BoardFile;
import com.example.exodia.board.domain.Category;
import com.example.exodia.board.dto.BoardDetailDto;
import com.example.exodia.board.dto.BoardListResDto;
import com.example.exodia.board.dto.BoardSaveReqDto;
import com.example.exodia.board.dto.BoardUpdateDto;
import com.example.exodia.board.repository.BoardRepository;
import com.example.exodia.board.repository.BoardFileRepository;
import com.example.exodia.comment.domain.Comment;
import com.example.exodia.comment.dto.CommentResDto;
import com.example.exodia.comment.repository.CommentRepository;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.common.service.UploadAwsFileService;
import com.example.exodia.user.domain.User;

import com.example.exodia.user.dto.UserDetailDto;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final UploadAwsFileService uploadAwsFileService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, UploadAwsFileService uploadAwsFileService, BoardFileRepository boardFileRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.boardRepository = boardRepository;
        this.uploadAwsFileService = uploadAwsFileService;
        this.boardFileRepository = boardFileRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    // 게시물 생성
    @Transactional
    public Board createBoard(BoardSaveReqDto dto, List<MultipartFile> files) {

        User user = userRepository.findByUserNum(dto.getUserNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));

        Category category = dto.getCategory();
        System.out.println(category);
        Board board = dto.toEntity(user,category);
        System.out.println(board.getCategory());
        board = boardRepository.save(board);
        System.out.println(board.getCategory());

        // 3. 파일이 있는 경우 파일 처리
        if (files != null && !files.isEmpty()) {
            // S3 파일 업로드 후 파일 경로 리스트 반환
            List<String> s3FilePaths = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(files);

            // BoardFile 엔티티를 생성하여 저장
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String s3FilePath = s3FilePaths.get(i);

                BoardFile boardFile = BoardFile.builder()
                        .board(board)
                        .filePath(s3FilePath)
                        .fileType(file.getContentType())
                        .fileName(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .build();

                boardFileRepository.save(boardFile);
            }
        }

        return board;
    }





    // 게시물 목록 조회 (검색 가능)
    public Page<BoardListResDto> BoardListWithSearch(Pageable pageable, String searchType, String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty()) {
            switch (searchType) {
                case "title":
                    return boardRepository.findByTitleContainingIgnoreCase(searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                case "content":
                    return boardRepository.findByContentContainingIgnoreCase(searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                case "title+content":
                    return boardRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                                    searchQuery, searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                case "user_num":
                    if (searchQuery.length() != 12) {
                        throw new IllegalArgumentException("사번은 12자리 문자열이어야 합니다.");
                    }
                    return boardRepository.findByUser_UserNumAndDelYn(searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                case "name":
                    return boardRepository.findByUser_NameContainingIgnoreCase(searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                default:
                    return boardRepository.findAllWithPinned(pageable).map(Board::listFromEntity);
            }
        } else {
            return boardRepository.findAllWithPinned(pageable).map(Board::listFromEntity);
        }
    }







    public BoardDetailDto BoardDetail(Long id) {
        // 게시물 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));

        // 관련 파일 목록 조회
        List<BoardFile> boardFiles = boardFileRepository.findByBoardId(id);
        List<String> filePaths = boardFiles.stream()
                .map(BoardFile::getFilePath)
                .collect(Collectors.toList());

        // 댓글 리스트 조회
        List<Comment> comments = commentRepository.findByBoardId(id);
        List<CommentResDto> commentResDto = comments.stream()
                .map(CommentResDto::fromEntity)
                .collect(Collectors.toList());

        // 게시물 상세 정보 생성
        BoardDetailDto boardDetailDto = board.detailFromEntity(filePaths);
        boardDetailDto.setComments(commentResDto);  // 댓글 리스트 추가

        return boardDetailDto;
    }


    @Transactional
    public void updateBoard(Long id, BoardUpdateDto dto, List<MultipartFile> files) {
        // 현재 사용자 정보 로그 출력
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("현재 사용자 번호: " + userNum);

        // 게시물 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        System.out.println("게시물 조회 완료. 게시물 제목: " + board.getTitle());

        // 사용자 권한 확인
        if (!board.getUser().getUserNum().equals(userNum)) {
            System.out.println("사용자 번호 불일치: " + userNum + " vs " + board.getUser().getUserNum());
            throw new IllegalArgumentException("작성자 본인만 수정할 수 있습니다.");
        }

        User user = userRepository.findByUserNum(board.getUser().getUserNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));
        Category category = dto.getCategory();
        board = dto.updateFromEntity(category,user);
        System.out.println(board.getCategory());
        board = boardRepository.save(board);


        boardFileRepository.deleteByBoardId(board.getId());

        List<String> s3FilePaths = null;

        if (files != null && !files.isEmpty()) {
            s3FilePaths = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(files);

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String s3FilePath = s3FilePaths.get(i);

                BoardFile boardFile = BoardFile.builder()
                        .board(board)
                        .filePath(s3FilePath)
                        .fileType(file.getContentType())
                        .fileName(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .build();

                boardFileRepository.save(boardFile);

            }
            boardRepository.save(board); // 게시물 저장
        }
    }




    @Transactional
    public void pinBoard(Long boardId, Long userId, boolean isPinned) {
        // 게시물 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!user.getDepartment().getName().equals("인사팀")) {
            throw new SecurityException("상단 고정은 인사팀만 가능합니다.");
        }

        if (!board.getCategory().equals(Category.NOTICE)) {
            throw new IllegalArgumentException("공지사항 게시물만 상단 고정이 가능합니다.");
        }


        board.setIsPinned(isPinned);
        boardRepository.save(board);
    }



    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("게시물을 찾을 수 없습니다."));
        boardRepository.delete(board);
    }
}
