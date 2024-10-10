package com.example.exodia.document.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.exodia.document.domain.Document;
import com.example.exodia.document.domain.DocumentType;
import com.example.exodia.document.domain.DocumentVersion;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
	// Document findByDocumentP_Id(Long documentPId);
	List<Document> findAllByDocumentVersion(DocumentVersion documentVersion);
	List<Document> findAllByDocumentTypeAndStatus(DocumentType documentType, String status);
	Page<Document> findAllByStatus(String status, Pageable pageable);
	List<Document> findByDocumentVersionAndIdGreaterThan(DocumentVersion documentVersion, Long id);
	List<Document> findAllByIdAndStatus(Long id, String status);

	// 검색
	@Query("SELECT d FROM Document d WHERE d.fileName LIKE %:keyword% OR d.description LIKE %:keyword%")
	List<Document> searchByKeyword(@Param("keyword") String keyword);
}
