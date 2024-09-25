package com.example.exodia.document.dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.exodia.document.domain.DocumentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocReqDto {
	private String typeName;
	private String fileName;
}
