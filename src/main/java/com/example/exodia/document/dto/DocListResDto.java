package com.example.exodia.document.dto;

import java.time.LocalDateTime;

import com.example.exodia.common.domain.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocListResDto extends BaseTimeEntity {
	private Long id;
	private String fileName;
	private String type;
	// private User user;
	private LocalDateTime updatedAt;
	private LocalDateTime viewedAt;
	private Long hits;
}
