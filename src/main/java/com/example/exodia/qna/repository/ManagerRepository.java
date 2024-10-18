package com.example.exodia.qna.repository;


import com.example.exodia.qna.domain.Manager;
import com.example.exodia.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    boolean existsByUser(User user);
}
