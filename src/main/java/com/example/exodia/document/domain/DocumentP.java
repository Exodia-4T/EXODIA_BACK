package com.example.exodia.document.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Where;

import com.example.exodia.common.domain.DelYN;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Where(clause = "del_yn = 'N'")
public class DocumentP {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 12)
	private Long id;

	private String version;

	@OneToOne
	@JoinColumn(name = "document_type_id", nullable = false)
	private DocumentType documentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "del_yn", nullable = false)
	private DelYN delYn = DelYN.N;

	public static DocumentP toEntity(Long id, DocumentType documentType) {
		return DocumentP.builder()
			.id(id)
			.version("1")
			.documentType(documentType)
			.delYn(DelYN.N)
			.build();
	}

	public DocumentP updateEntity(Long id, String version) {
		return DocumentP.builder()
			.id(id)
			.version(String.valueOf(Integer.parseInt(version) + 1))
			.documentType(documentType)
			.delYn(DelYN.N)
			.build();
	}
}
