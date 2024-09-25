package com.example.exodia.submit.controller;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exodia.common.dto.CommonErrorDto;
import com.example.exodia.submit.dto.SubmitSaveReqDto;
import com.example.exodia.submit.dto.SubmitStatusUpdateDto;
import com.example.exodia.submit.service.SubmitService;

@RestController
@RequestMapping("/submit")
public class SubmitController {

	private final SubmitService submitService;

	@Autowired
	public SubmitController(SubmitService submitService) {
		this.submitService = submitService;
	}

	// 	결재 라인 수정
	@PostMapping("/create")
	public ResponseEntity<?> createSubmit(@RequestBody SubmitSaveReqDto submitSaveReqDto) {
		try {
			submitService.createSubmit(submitSaveReqDto);
			return ResponseEntity.ok("결재 요청 성공");
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateSubmit(@RequestBody SubmitStatusUpdateDto submitStatusUpdateDto) {
		try {
			submitService.updateSubmit(submitStatusUpdateDto);
			return ResponseEntity.ok("결재 상태 변경 성공");
		}catch(EntityNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(new CommonErrorDto(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

}
