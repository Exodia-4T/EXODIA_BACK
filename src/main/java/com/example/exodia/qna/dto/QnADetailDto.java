package com.example.exodia.qna.dto;

import com.example.exodia.board.dto.FileDto;
import com.example.exodia.comment.dto.CommentResDto;
import com.example.exodia.qna.domain.QnA;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.exodia.board.dto.FileDto.convertFileListToDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QnADetailDto {
    private Long id;
    private String title;
    private String questionText;
    private String answerText;
    private String questionUserName;
    private String answerUserName;
    private String questionUserNum;
    private String answerUserNum;
    private String departmentName; // 부서명 필드 추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime answeredAt;
    private List<FileDto> qFiles;
    private List<FileDto> aFiles;
    private List<CommentResDto> comments;
    @Builder.Default
    private Boolean anonymous = false;

    // QnA 엔티티를 QnADetailDto로 변환하는 메서드
    public static QnADetailDto fromEntity(QnA qna, List<CommentResDto> comments) {
        return QnADetailDto.builder()
                .id(qna.getId())
                .title(qna.getTitle())
                .questionText(qna.getQuestionText())
                .answerText(qna.getAnswerText())
                .questionUserName(qna.getQuestioner().getName()) // 질문자 이름
                .answerUserName(qna.getAnswerer() != null ? qna.getAnswerer().getName() : null) // 답변자가 없을 수 있으므로 null 체크
                .createdAt(qna.getCreatedAt())
                .updatedAt(qna.getUpdatedAt())
                .answeredAt(qna.getAnsweredAt())
                .qFiles(convertFileListToDto(qna.getQuestionerFiles())) // 질문자 파일 리스트를 FileDto 리스트로 변환
                .aFiles(convertFileListToDto(qna.getAnswererFiles())) // 답변자 파일 리스트를 FileDto 리스트로 변환
                .comments(comments != null ? comments : List.of()) // 댓글 리스트 null 체크
                .departmentName(qna.getDepartment().getName()) // 부서명 설정
                .questionUserNum(qna.getQuestioner().getUserNum())
                .answerUserNum(qna.getAnswerer() != null ? qna.getAnswerer().getUserNum() : null)
                .anonymous(qna.getAnonymous())
                .build();
    }
}
