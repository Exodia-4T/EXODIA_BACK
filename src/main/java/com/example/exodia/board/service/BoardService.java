package com.example.exodia.board.service;

import com.example.exodia.board.domain.Board;
import com.example.exodia.board.domain.BoardFile;
import com.example.exodia.board.domain.Category;
import com.example.exodia.board.dto.BoardDetailDto;
import com.example.exodia.board.dto.BoardListResDto;
import com.example.exodia.board.dto.BoardSaveReqDto;
import com.example.exodia.board.dto.BoardUpdateDto;
import com.example.exodia.board.repository.BoardFileRepository;
import com.example.exodia.board.repository.BoardRepository;
import com.example.exodia.comment.domain.Comment;
import com.example.exodia.comment.dto.CommentResDto;
import com.example.exodia.comment.repository.CommentRepository;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.common.service.UploadAwsFileService;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
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
        User user = null;
        boolean isAnonymous = dto.isAnonymous();
        Category category = dto.getCategory();

        // 익명이 아닌 경우에만 User 정보를 조회
        if (!isAnonymous) {
            user = userRepository.findByUserNum(dto.getUserNum())
                    .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));

            // 카테고리가 NOTICE 또는 FAMILY_EVENT일 때, 부서가 인사팀인지 확인
            if ((category == Category.NOTICE || category == Category.FAMILY_EVENT) &&
                    !user.getDepartment().getName().equals("인사팀")) {
                throw new SecurityException("공지사항 또는 경조사 게시물은 인사팀만 작성할 수 있습니다.");
            }
        }
        System.out.println(user.getDepartment().getName());

        // BoardSaveReqDto에서 엔티티 변환
        Board board = dto.toEntity(user, category);
        board = boardRepository.save(board);

        // 파일이 있는 경우 파일 처리
        if (files != null && !files.isEmpty()) {
            // S3 파일 업로드 후 파일 경로 및 Presigned URL 반환
            List<String> s3FilePaths = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(files);

            // 파일 이름 리스트 추출
            List<String> fileNames = files.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .collect(Collectors.toList());

            List<String> presignedUrls = uploadAwsFileService.generatePresignedUrls(fileNames); // Presigned URL 생성

            // BoardFile 엔티티를 생성하여 저장
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String s3FilePath = s3FilePaths.get(i);
                String fileDownloadUrl = presignedUrls.get(i);  // 다운로드 URL

                // BoardFile 엔티티 생성 및 저장
                BoardFile boardFile = BoardFile.createBoardFile(
                        board,
                        s3FilePath,
                        file.getContentType(),
                        file.getOriginalFilename(),
                        file.getSize(),
                        fileDownloadUrl // Presigned URL을 다운로드 URL로 추가
                );

                // BoardFile 저장
                boardFileRepository.save(boardFile);
            }
        }

        // 최종적으로 저장된 Board 엔티티 반환
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
                    // 사번이 12자리인지 확인하는 로직
                    if (searchQuery.length() != 12) {
                        throw new IllegalArgumentException("사번은 12자리 문자열이어야 합니다.");
                    }
                    return boardRepository.findByUser_UserNumAndDelYn(searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                case "name":
                    return boardRepository.findByUser_NameContainingIgnoreCase(searchQuery, DelYN.N, pageable)
                            .map(Board::listFromEntity);
                default:
                    return boardRepository.findAllWithPinned(pageable).map(Board::listFromEntity);  // 상단 고정 적용된 쿼리
            }
        } else {
            return boardRepository.findAllWithPinned(pageable).map(Board::listFromEntity);  // 상단 고정 적용된 쿼리
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
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();

        // 게시물 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));

        // 사용자 권한 확인
        if (!board.getUser().getUserNum().equals(userNum)) {
            System.out.println("사용자 번호 불일치: " + userNum + " vs " + board.getUser().getUserNum());
            throw new IllegalArgumentException("작성자 본인만 수정할 수 있습니다.");
        }

        User user = userRepository.findByUserNum(board.getUser().getUserNum())
                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));
        Category category = dto.getCategory();

        // 카테고리가 NOTICE 또는 FAMILY_EVENT일 때, 부서가 인사팀인지 확인
        if ((category == Category.NOTICE || category == Category.FAMILY_EVENT) &&
                !user.getDepartment().getName().equals("인사팀")) {
            throw new SecurityException("공지사항 또는 가족 행사 게시물은 인사팀만 작성할 수 있습니다.");
        }

        board = dto.updateFromEntity(category,user);
        System.out.println(board.getCategory());
        board = boardRepository.save(board);


        boardFileRepository.deleteByBoardId(board.getId());

        List<String> s3FilePaths = null;
        // Step 3: 새로운 파일이 있는 경우 처리
        if (files != null && !files.isEmpty()) {
            s3FilePaths = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(files);

            // 새로운 파일 저장
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

        // 게시물의 카테고리가 NOTICE 또는 FAMILY_EVENT일 때, 부서가 인사팀인지 확인
        if ((board.getCategory() == Category.NOTICE || board.getCategory() == Category.FAMILY_EVENT) &&
                !board.getUser().getDepartment().getName().equals("인사팀")) {
            throw new SecurityException("공지사항 또는 가족 행사 게시물은 인사팀만 삭제할 수 있습니다.");
        }

        // 게시물 삭제
        boardRepository.delete(board);
    }

}
