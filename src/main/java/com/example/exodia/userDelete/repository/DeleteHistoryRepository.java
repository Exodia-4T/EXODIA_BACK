package com.example.exodia.userDelete.repository;

import com.example.exodia.userDelete.domain.DeleteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeleteHistoryRepository extends JpaRepository<DeleteHistory, Long> {
}
