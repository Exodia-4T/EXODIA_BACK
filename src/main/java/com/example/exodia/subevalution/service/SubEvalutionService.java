package com.example.exodia.subevalution.service;

import com.example.exodia.evalutionm.domain.Evalutionm;
import com.example.exodia.evalutionm.repository.EvalutionmRepository;
import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.subevalution.dto.SubEvalutionDto;
import com.example.exodia.subevalution.dto.SubEvalutionUpdateDto;
import com.example.exodia.subevalution.repository.SubEvalutionRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubEvalutionService {
    private final SubEvalutionRepository subEvalutionRepository; // 소분류
    private final EvalutionmRepository evalutionmRepository; // 중분류
    private final UserRepository userRepository;

    public SubEvalutionService(SubEvalutionRepository subEvalutionRepository, EvalutionmRepository evalutionmRepository, UserRepository userRepository) {
        this.subEvalutionRepository = subEvalutionRepository;
        this.evalutionmRepository = evalutionmRepository;
        this.userRepository = userRepository;
    }

    /* 소분류 content 생성 */
    @Transactional
    public SubEvalution createSubEvalution(SubEvalutionDto subEvalutionDto) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));

        Evalutionm evalutionm = evalutionmRepository.findById(subEvalutionDto.getEvalutionmId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중분류 : " + subEvalutionDto.getEvalutionmId()));

        SubEvalution subEvalution = subEvalutionDto.toEntity(evalutionm, user);

        return subEvalutionRepository.save(subEvalution);
    }

    /* 소분류 조회 */
    @Transactional(readOnly = true)
    public List<SubEvalution> getAllSubEvalutionsForUser() {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));

        // 사용자가 작성한 모든 소분류 조회
        return subEvalutionRepository.findByUser(user);
    }

    @Transactional
    public SubEvalution updateSubEvalution(SubEvalutionUpdateDto subEvalutionUpdateDto) {
        SubEvalution subEvalution = subEvalutionRepository.findById(subEvalutionUpdateDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid subEvalution ID: " + subEvalutionUpdateDto.getId()));

        // 내용 업데이트
        subEvalution.setContent(subEvalutionUpdateDto.getContent());

        // 수정된 소분류 저장
        return subEvalutionRepository.save(subEvalution);
    }

    @Transactional
    public void deleteSubEvalution(Long id) {
        SubEvalution subEvalution = subEvalutionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid subEvalution ID: " + id));

        subEvalutionRepository.delete(subEvalution);
    }
}
