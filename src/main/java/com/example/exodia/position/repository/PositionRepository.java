package com.example.exodia.position.repository;

import java.util.Optional;

import com.example.exodia.position.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionRepository extends JpaRepository<Position, Long> {
	Optional<Position> findByName(String position);
}
