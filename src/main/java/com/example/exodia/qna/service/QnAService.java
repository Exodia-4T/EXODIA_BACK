package com.example.exodia.qna.service;

import com.example.exodia.board.domain.BoardFile;
import com.example.exodia.board.repository.BoardFileRepository;
import com.example.exodia.comment.domain.Comment;
import com.example.exodia.comment.dto.CommentResDto;
import com.example.exodia.comment.repository.CommentRepository;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.common.service.UploadAwsFileService;
import com.example.exodia.department.domain.Department;
import com.example.exodia.department.repository.DepartmentRepository;
import com.example.exodia.qna.domain.QnA;
import com.example.exodia.qna.dto.*;
import com.example.exodia.qna.repository.QnARepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QnAService {

    private final QnARepository qnARepository;
    private final CommentRepository commentRepository;
    private final UploadAwsFileService uploadAwsFileService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final BoardFileRepository boardFileRepository;

    @Autowired
    public QnAService(QnARepository qnARepository, CommentRepository commentRepository,
                      UploadAwsFileService uploadAwsFileService, UserRepository userRepository,
                      DepartmentRepository departmentRepository, BoardFileRepository boardFileRepository) {
        this.qnARepository = qnARepository;
        this.commentRepository = commentRepository;
        this.uploadAwsFileService = uploadAwsFileService;
        this.userRepository = userRepository;
        this.boardFileRepository = boardFileRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public QnA createQuestion(QnASaveReqDto dto, List<MultipartFile> files) {
        // 유저 정보 가져오기
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));

        // dto.getDepartmentId()로 Department 객체 조회
        if (dto.getDepartmentId() == null || dto.getDepartmentId() == 0) {
            throw new IllegalArgumentException("유효하지 않은 부서 ID 입니다.");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 부서를 찾을 수 없습니다."));

        // QnA 객체 생성
        QnA qna = dto.toEntity(user, department);

        // 파일 처리 로직 (필요 시 추가)
        files = files == null ? Collections.emptyList() : files;
        if (!files.isEmpty()) {
            List<String> s3FilePaths = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(files, "qna");
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) {
                    continue;
                }

                String s3FilePath = s3FilePaths.get(i);
                BoardFile boardFile = BoardFile.createQuestionFile(qna, s3FilePath, file.getContentType(),
                        file.getOriginalFilename(), file.getSize());
                qna.getQuestionerFiles().add(boardFile);
                boardFileRepository.save(boardFile);
            }
        }

        return qnARepository.save(qna);
    }



    public Page<QnAListResDto> qnaListByGroup(Long departmentId, Pageable pageable) {
        Department department = departmentRepository.findDepartmentById(departmentId);
        Page<QnA> qnAS = qnARepository.findAllByDepartmentIdAndDelYN(department.getId(), DelYN.N, pageable);
        return qnAS.map(QnA::listFromEntity);
    }

    public Page<QnAListResDto> qnaListWithSearch(Pageable pageable, String searchType, String searchQuery) {
        // 로그 추가: 입력값 확인
        System.out.println("qnaListWithSearch() - Received searchType: " + searchType + ", searchQuery: " + searchQuery);

        Page<QnA> qnAS;

        // 1. searchQuery 및 searchType 확인
        if (searchQuery == null || searchQuery.isEmpty()) {
            System.out.println("qnaListWithSearch() - searchQuery is null or empty, returning sorted by date.");

            Pageable sortedByDate = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
            qnAS = qnARepository.findByDelYN(DelYN.N, sortedByDate);
        } else {
            System.out.println("qnaListWithSearch() - searchQuery is not empty. Performing search based on searchType.");

            // 2. searchType에 따른 검색 로직 분기 및 메서드 반환값 확인
            qnAS = switch (searchType) {
                case "title" -> {
                    System.out.println("qnaListWithSearch() - Searching by title.");
                    Page<QnA> titleResult = qnARepository.findByTitleContainingIgnoreCaseAndDelYN(searchQuery, DelYN.N, pageable);
                    System.out.println("qnaListWithSearch() - Result count: " + titleResult.getTotalElements());
                    yield titleResult;
                }
                case "content" -> {
                    System.out.println("qnaListWithSearch() - Searching by content.");
                    Page<QnA> contentResult = qnARepository.findByQuestionTextContainingIgnoreCaseAndDelYN(searchQuery, DelYN.N, pageable);
                    System.out.println("qnaListWithSearch() - Result count: " + contentResult.getTotalElements());
                    yield contentResult;
                }
                default -> {
                    System.out.println("qnaListWithSearch() - Invalid searchType, returning all results.");
                    Page<QnA> defaultResult = qnARepository.findByDelYN(DelYN.N, pageable);
                    System.out.println("qnaListWithSearch() - Result count: " + defaultResult.getTotalElements());
                    yield defaultResult;
                }
            };
        }

        // 3. 검색 결과를 DTO로 변환하고, 변환된 값이 null이 아닌지 확인
        if (qnAS == null) {
            System.out.println("qnaListWithSearch() - qnAS is null after search.");
            throw new NullPointerException("검색 결과가 null입니다.");
        }

        Page<QnAListResDto> qnaListResDtoPage = qnAS.map(QnA::listFromEntity);

        // 4. DTO로 변환된 결과가 null인 경우 확인
        qnaListResDtoPage.forEach(dto -> {
            if (dto == null) {
                System.out.println("qnaListWithSearch() - Null DTO found in result.");
            } else {
                System.out.println("qnaListWithSearch() - QnAListResDto 반환 - ID: " + dto.getId() + ", departmentName: " + dto.getDepartmentName());
            }
        });

        return qnaListResDtoPage;
    }






    public List<QnAListResDto> getUserQnAs() {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));
        List<QnA> qnAs = qnARepository.findByQuestioner(user);
        return qnAs.stream().map(QnA::listFromEntity).collect(Collectors.toList());
    }

    public QnADetailDto getQuestionDetail(Long id) {
        QnA qna = qnARepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
        List<Comment> comments = commentRepository.findByQnaId(id);
        List<CommentResDto> commentResDto = comments.stream().map(CommentResDto::fromEntity).collect(Collectors.toList());
        return QnADetailDto.fromEntity(qna, commentResDto);
    }

    @Transactional
    public QnA answerQuestion(Long id, QnAAnswerReqDto dto, List<MultipartFile> files) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        QnA qna = qnARepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Department questionerDepartment = qna.getQuestioner().getDepartment();
        User answerer = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));

        if (!answerer.getDepartment().getId().equals(questionerDepartment.getId())) {
            throw new SecurityException("다른 부서의 질문에 답변할 권한이 없습니다.");
        }

        qna.setAnswerText(dto.getAnswerText());
        qna.setAnsweredAt(LocalDateTime.now());
        qna.setAnswerer(answerer);

        files = files == null ? Collections.emptyList() : files;
        if (!files.isEmpty()) {
            List<String> s3FilePaths = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(files, "qna");
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) {
                    System.out.println("빈 파일이므로 업로드를 건너뜁니다. 파일 이름: " + file.getOriginalFilename());
                    continue;
                }

                String s3FilePath = s3FilePaths.get(i);
                BoardFile boardFile = BoardFile.createAnswerFile(qna, s3FilePath, file.getContentType(),
                        file.getOriginalFilename(), file.getSize());
                qna.getAnswererFiles().add(boardFile);
                boardFileRepository.save(boardFile);
            }
        }

        return qnARepository.save(qna);
    }

    @Transactional
    public void QnAQUpdate(Long id, QnAQtoUpdateDto dto, List<MultipartFile> files) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        QnA qna = qnARepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        if (!qna.getQuestioner().getUserNum().equals(userNum)) {
            throw new IllegalArgumentException("작성자 본인만 수정할 수 있습니다.");
        }

        qna.QnAQUpdate(dto);

        files = files == null ? Collections.emptyList() : files;
        if (!files.isEmpty()) {
            boardFileRepository.deleteAll(qna.getQuestionerFiles());
            qna.getQuestionerFiles().clear();

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    System.out.println("빈 파일이므로 업로드를 건너뜁니다. 파일 이름: " + file.getOriginalFilename());
                    continue;
                }

                String s3FilePath = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(List.of(file), "qna").get(0);
                BoardFile boardFile = BoardFile.createQuestionFile(qna, s3FilePath, file.getContentType(),
                        file.getOriginalFilename(), file.getSize());
                qna.getQuestionerFiles().add(boardFile);
                boardFileRepository.save(boardFile);
            }
        }

        qnARepository.save(qna);
    }

    @Transactional
    public void QnAAUpdate(Long id, QnAAtoUpdateDto dto, List<MultipartFile> files) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        QnA qna = qnARepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        if (!qna.getAnswerer().getUserNum().equals(userNum)) {
            throw new IllegalArgumentException("작성자 본인만 수정할 수 있습니다.");
        }

        qna.QnAAUpdate(dto);

        files = files == null ? Collections.emptyList() : files;
        if (!files.isEmpty()) {
            boardFileRepository.deleteAll(qna.getAnswererFiles());
            qna.getAnswererFiles().clear();

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    System.out.println("빈 파일이므로 업로드를 건너뜁니다. 파일 이름: " + file.getOriginalFilename());
                    continue;
                }

                String s3FilePath = uploadAwsFileService.uploadMultipleFilesAndReturnPaths(List.of(file), "qna").get(0);
                BoardFile boardFile = BoardFile.createAnswerFile(qna, s3FilePath, file.getContentType(),
                        file.getOriginalFilename(), file.getSize());
                qna.getAnswererFiles().add(boardFile);
                boardFileRepository.save(boardFile);
            }
        }

        qnARepository.save(qna);
    }

    @Transactional
    public QnA qnaDelete(Long id) {
        QnA qna = qnARepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));
        qna.updateDelYN(DelYN.Y);
        return qna;
    }
}
