package com.example.exodia.qna.repository;

import com.example.exodia.common.domain.DelYN;
import com.example.exodia.qna.domain.QnA;
import com.example.exodia.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QnARepository extends JpaRepository<QnA, Long> {

    List<QnA> findByQuestioner(User user);
    Page<QnA> findByDelYN(DelYN delYN, Pageable pageable);

    // 페이징 처리를 위한 전체 조회
    Page<QnA> findAll(Pageable pageable);

    // 제목 검색
    Page<QnA> findByTitleContainingIgnoreCaseAndDelYN(String title, DelYN delYN, Pageable pageable);

    // 질문 내용 검색
    Page<QnA> findByQuestionTextContainingIgnoreCaseAndDelYN(String questionText, DelYN delYN, Pageable pageable);


    // 제목 또는 질문 내용으로 검색하고 삭제 여부에 따라 필터링
    Page<QnA> findByTitleContainingIgnoreCaseOrQuestionTextContainingIgnoreCaseAndDelYN(
            String title, String questionText, DelYN delYN, Pageable pageable);



    Page<QnA> findAllByDepartmentIdAndDelYN(Long id, DelYN delYN, Pageable pageable);

}