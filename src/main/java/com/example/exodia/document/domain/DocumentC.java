package com.example.exodia.document.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.document.dto.DocDetailResDto;
import com.example.exodia.document.dto.DocListResDto;
import com.example.exodia.document.dto.DocReqDto;
import com.example.exodia.document.dto.DocUpdateReqDto;
import com.example.exodia.user.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;

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
public class DocumentC extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 12)
	private Long id;

	@Column(nullable = false)
	private String fileName;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String contents;

	@Column(nullable = false)
	private String filePath;

	private String description;

	// @Column(nullable = false)
	// @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	// private LocalDateTime saveDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;	// 최근 수정 시간

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "viewed_at")
	private LocalDateTime viewedAt;	// 최근 열람 시간

	@Enumerated(EnumType.STRING)
	@Column(name = "del_yn", nullable = false)
	private DelYN delYn = DelYN.N;

	@ColumnDefault("0")
	private int hits;

	@OneToOne
	private DocumentType documentType;

	// @ManyToOne
	// @JoinColumn(name = "user_id", nullable = false)
	// private User user;
	//

	@OneToOne
	@JoinColumn(name = "document_p_id")
	private DocumentP documentP;

	//
	// @ManyToOne
	// @JoinColumn(name = "department_id", nullable = false)
	// private Department department;

	public static DocumentC toEntity(DocReqDto docReqDto, String path, String contents, DocumentType documentType) {
		return DocumentC.builder()
			.fileName(docReqDto.getFileName())
			.filePath(path)
			.contents(contents)
			.documentType(documentType)
			.updatedAt(LocalDateTime.now())
			.viewedAt(LocalDateTime.now())
			.delYn(DelYN.N)
			.build();
	}

	public DocDetailResDto fromEntity(){
		return DocDetailResDto.builder()
			.fileName(this.fileName)
			.fileExtension(this.fileName.substring(fileName.lastIndexOf(".") + 1))
			.updatedAt(this.getUpdatedAt())
			.viewedAt(this.getViewedAt())
			.description(this.description).build();
	}

	public DocListResDto fromEntityList(){
		return DocListResDto.builder()
			.id(this.id)
			.fileName(this.fileName)
			// .User(this.user)
			.updatedAt(this.getUpdatedAt())
			.viewedAt(this.getViewedAt())
			.build();
	}

	public static DocumentC updatetoEntity(DocUpdateReqDto docUpdateReqDto, String path, String contents,
		DocumentType documentType){
		return DocumentC.builder()
			.fileName(docUpdateReqDto.getFileName())
			.contents(contents)
			.filePath(path)
			.documentType(documentType)
			.updatedAt(LocalDateTime.now())
			.viewedAt(LocalDateTime.now())
			.delYn(DelYN.N)
			.build();
	}


	public void updateViewdAt(){
		this.viewedAt = LocalDateTime.now();
	}

	public void updateUpdatedAt(){
		this.updatedAt = LocalDateTime.now();
	}

	public void setParentDocument(DocumentP parent) {
		this.documentP = parent;
	}

	public void updateDocumentP(DocumentP updateP) {
		this.documentP = updateP;
	}
}

