package com.example.exodia.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.exodia.document.domain.DocumentC;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface DocumentCRepository extends JpaRepository<DocumentC,Long> {
	// 전체 문서 조회
	@Query("SELECT d FROM DocumentC d WHERE d.createdAt = d.updatedAt")
	List<DocumentC> findDocsWhereCreatedAtEqualUpdatedAt();

	// 최근 열람 문서 조회 (열람 시간이 가장 최근인 문서)
	List<DocumentC> findByOrderByViewedAtDesc();

	// 최근 수정 문서 조회 (수정 시간이 가장 최근인 문서)
	List<DocumentC> findByOrderByUpdatedAtDesc();
}
