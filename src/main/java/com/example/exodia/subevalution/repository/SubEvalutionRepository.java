package com.example.exodia.subevalution.repository;

import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubEvalutionRepository extends JpaRepository<SubEvalution, Long> {
    List<SubEvalution> findByUser(User user);
}
