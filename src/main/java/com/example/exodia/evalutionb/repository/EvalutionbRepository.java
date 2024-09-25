package com.example.exodia.evalutionb.repository;

import com.example.exodia.evalutionb.domain.Evalutionb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvalutionbRepository extends JpaRepository<Evalutionb, Long> {
}