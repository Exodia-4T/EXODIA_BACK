package com.example.exodia.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exodia.document.domain.DocumentP;

@Repository
public interface DocumentPRepository extends JpaRepository<DocumentP, Long> {
}
