package com.example.exodia.evalution.domain;

import com.example.exodia.attendance.domain.DayStatus;
import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.department.domain.Department;
import com.example.exodia.evalution.dto.EvalutionDto;
import com.example.exodia.position.domain.Position;
import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Evalution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Score score; // 평가 점수

    @ManyToOne
    @JoinColumn(name = "target_user_id", nullable = false)
    private User target; // 평가 대상자

    @ManyToOne
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator; // 평가자

    @ManyToOne
    @JoinColumn(name = "sub_evalution_id", nullable = false)
    private SubEvalution subEvalution; // 소분류

//    @ManyToOne
//    @JoinColumn(name = "department_id", nullable = false)
//    private Department department; // 대상자의 부서
//
//    @ManyToOne
//    @JoinColumn(name = "position_id", nullable = false)
//    private Position position; // 대상자의 직급


    public static EvalutionDto fromEntity(Evalution evalution) {
        return EvalutionDto.builder()
                .id(evalution.getId())
                .subEvalutionId(evalution.getSubEvalution().getId())
                .targetName(evalution.getTarget().getName())
                .targetDepartment(evalution.getTarget().getDepartment().getName())
                .evaluatorName(evalution.getEvaluator().getName())
                .evaluatorDepartment(evalution.getEvaluator().getDepartment().getName())
                .score(evalution.getScore())
                .build();
    }
}