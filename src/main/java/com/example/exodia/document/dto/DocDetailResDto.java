package com.example.exodia.document.dto;

import java.time.LocalDateTime;

import javax.swing.text.Document;

import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.document.domain.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocDetailResDto extends BaseTimeEntity {
	private String fileName;
	private String fileExtension;
	private LocalDateTime updatedAt;
	private LocalDateTime viewedAt;
	private DocumentType documentType;
	// private User user;
	private String description;	// 설명
}
