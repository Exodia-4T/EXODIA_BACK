package com.example.exodia.evalutionavg.repository;

import com.example.exodia.evalutionavg.domain.EvalutionAvg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvalutionAvgRepository extends JpaRepository<EvalutionAvg, Integer> {
}
