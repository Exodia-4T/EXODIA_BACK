package com.example.exodia.submit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exodia.submit.domain.Submit;

@Repository
public interface SubmitRepository extends JpaRepository<Submit, Long> {
}
