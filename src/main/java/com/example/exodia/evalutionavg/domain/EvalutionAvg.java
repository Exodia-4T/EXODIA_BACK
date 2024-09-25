package com.example.exodia.evalutionavg.domain;


import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.subevalution.domain.SubEvalution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

/*
* 사실상 이전 데이터 evalutionb, evalutionm은 자동으로 입력되고
* sub-evalution, evalution 은 휘발성 데이터를 지향 하므로
* 이 evalutionAvg 에서 최종 평점 처리를 진행 + 인사평가가 열리는 기간 설정을 위한 날짜 받기
* */

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "del_yn = 'N'")
public class EvalutionAvg extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sub_evalution_id", nullable = false)
    private SubEvalution subEvalution; // 소분류

    @Column(nullable = false)
    private double average; // 평균 점수

    @Column(nullable = false)
    private LocalDate startDate; // 평가 시작일

    @Column(nullable = false)
    private LocalDate endDate; // 평가 종료일
}
