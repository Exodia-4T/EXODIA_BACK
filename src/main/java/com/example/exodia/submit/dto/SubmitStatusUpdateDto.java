package com.example.exodia.submit.dto;

import com.example.exodia.submit.domain.SubmitStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitStatusUpdateDto {
	private Long submitId;
	private SubmitStatus status;
}

