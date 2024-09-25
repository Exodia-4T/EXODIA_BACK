package com.example.exodia.evalution.service;

import com.example.exodia.department.domain.Department;
import com.example.exodia.evalution.domain.Evalution;
import com.example.exodia.evalution.dto.EvalutionDto;
import com.example.exodia.evalution.repository.EvalutionRepository;
import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.subevalution.repository.SubEvalutionRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvalutionService {
    private final EvalutionRepository evalutionRepository;
    private final SubEvalutionRepository subEvalutionRepository;
    private final UserRepository userRepository;

    public EvalutionService(EvalutionRepository evalutionRepository, SubEvalutionRepository subEvalutionRepository, UserRepository userRepository) {
        this.evalutionRepository = evalutionRepository;
        this.subEvalutionRepository = subEvalutionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Evalution createEvalution(EvalutionDto evalutionDto) {
        // 현재 인증된 사용자 가져오기 (평가자)
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User evaluator = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));

        // 평가할 소분류 조회
        SubEvalution subEvalution = subEvalutionRepository.findById(evalutionDto.getSubEvalutionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 소분류: " + evalutionDto.getSubEvalutionId()));

        // 평가 대상자 조회
        User target = userRepository.findById(evalutionDto.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대상자 ID: " + evalutionDto.getTargetUserId()));

        // 평가 권한 확인
        if (!canEvaluate(evaluator, target)) {
            throw new RuntimeException("평가할 권한이 없습니다.");
        }

        // DTO를 통해 엔티티 생성
        Evalution evalution = evalutionDto.toEntity(subEvalution, evaluator, target);
        return evalutionRepository.save(evalution);
    }

    // 평가 권한을 확인하는 메서드
    private boolean canEvaluate(User evaluator, User target) {
        // 자기 자신 평가 권한
        if (evaluator.equals(target)) {
            return true;
        }
        // 같은 부서의 팀장인지 확인
        if (evaluator.getDepartment().equals(target.getDepartment())
                && evaluator.getPosition().getName().equals("팀장")) {
            return true;
        }


//        // 상위 부서의 명칭..인지 확인
//        Department parentDepartment = target.getDepartment().getParentDepartment();
//        while (parentDepartment != null) {
//            if (evaluator.getDepartment().equals(parentDepartment)
//                    && evaluator.getPosition().getName().equals("소장")) {
//                return true;
//            }
//            parentDepartment = parentDepartment.getParentDepartment();
//        }
        return false;
    }

    /* 평가 조회 */
    @Transactional(readOnly = true)
    public List<EvalutionDto> getAllEvalutions() {
        List<Evalution> evalutions = evalutionRepository.findAll();
        return evalutions.stream().map(Evalution::fromEntity).collect(Collectors.toList());
    }
}