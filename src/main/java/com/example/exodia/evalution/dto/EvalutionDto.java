package com.example.exodia.evalution.dto;

import com.example.exodia.evalution.domain.Evalution;
import com.example.exodia.evalution.domain.Score;
import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalutionDto {
    private Long id; // 평가 ID
    private Long subEvalutionId; // 소분류 ID
    private Long targetUserId; // 평가 대상자 ID
    private String targetName; // 평가 대상자 이름
    private String targetDepartment; // 평가 대상자 부서 이름
    private String evaluatorName; // 평가자 이름
    private String evaluatorDepartment; // 평가자 부서 이름
    private Score score; // 평가 점수

    public Evalution toEntity(SubEvalution subEvalution, User evaluator, User target) {
        return Evalution.builder()
                .subEvalution(subEvalution)
                .evaluator(evaluator)
                .target(target)
                .score(this.score)
                .build();
    }
}