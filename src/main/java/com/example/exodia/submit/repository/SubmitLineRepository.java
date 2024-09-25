package com.example.exodia.submit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.exodia.submit.domain.SubmitLine;

@Repository
public interface SubmitLineRepository extends JpaRepository<SubmitLine, Long> {
	Optional<SubmitLine> findByUserNumAndSubmitId(String userNum, Long submitId);
	List<SubmitLine> findBySubmitId(Long submitId);

}
