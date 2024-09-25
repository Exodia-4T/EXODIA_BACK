package com.example.exodia.submit.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.exodia.common.auth.JwtTokenProvider;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.position.domain.Position;
import com.example.exodia.position.repository.PositionRepository;
import com.example.exodia.submit.domain.Submit;
import com.example.exodia.submit.domain.SubmitLine;
import com.example.exodia.submit.domain.SubmitStatus;
import com.example.exodia.submit.dto.SubmitSaveReqDto;
import com.example.exodia.submit.dto.SubmitStatusUpdateDto;
import com.example.exodia.submit.repository.SubmitLineRepository;
import com.example.exodia.submit.repository.SubmitRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;

@Service
public class SubmitService {

	private final SubmitRepository submitRepository;
	private final UserRepository userRepository;
	private final PositionRepository positionRepository;
	private final SubmitLineRepository submitLineRepository;

	public SubmitService(SubmitRepository submitRepository, UserRepository userRepository,
		PositionRepository positionRepository, SubmitLineRepository submitLineRepository,
		JwtTokenProvider jwtTokenProvider) {
		this.submitRepository = submitRepository;
		this.userRepository = userRepository;
		this.positionRepository = positionRepository;
		this.submitLineRepository = submitLineRepository;
	}

	// 	결재라인 등록
	public Submit createSubmit(SubmitSaveReqDto dto) {
		String userNum = SecurityContextHolder.getContext().getAuthentication().getName();

		User submitUser = userRepository.findByUserNum(userNum)
			.orElseThrow(() -> new EntityNotFoundException("회원정보가 존재하지 않습니다."));
		// Submit submit = Submit.builder().user(submitUser).submitLines(new ArrayList<>()).build();
		Submit submit = dto.toEntity(submitUser);
		SubmitLine submitLine = SubmitLine.builder().build();

		for (SubmitSaveReqDto.SubmitUserDto userDto : dto.getSubmitUserDtos()) {
			Position position = positionRepository.findByName(userDto.getPosition())
				.orElseThrow(() -> new EntityNotFoundException("직급 정보가 존재하지 않습니다."));

			User user = userRepository.findByNameAndPosition(userDto.getUserName(), position)
				.orElseThrow(() -> new EntityNotFoundException("회원정보가 존재하지 않습니다."));

			submitLine = dto.toLineEntity(user);

			submit.getSubmitLines().add(submitLine);
			submitLine.updateSubmit(submit);
		}

		submitRepository.save(submit);
		submitLineRepository.save(submitLine);
		return submit;
	}

	// 결재 상태 변경
	public SubmitLine updateSubmit(SubmitStatusUpdateDto dto) {
		String userNum = SecurityContextHolder.getContext().getAuthentication().getName();	// 사용자
		SubmitLine submitLine = submitLineRepository.findByUserNumAndSubmitId(userNum, dto.getSubmitId())
			.orElseThrow(() -> new EntityNotFoundException("결재 정보가 존재하지 않습니다."));
		Submit submit = submitLine.getSubmit();

		if (dto.getStatus() == SubmitStatus.ACCEPT) {
			// 이전 레벨의 결재가 완료된건지 확인 조건문 필요
			submitLine.updateStatus(dto.getStatus());
			submitLineRepository.save(submitLine);
		}else if(dto.getStatus() == SubmitStatus.REJECT) {
			List<SubmitLine> submitLines = submitLineRepository.findBySubmitId(dto.getSubmitId());
			for(SubmitLine line : submitLines) {
				submitLineRepository.save(line);
				line.updateStatus(dto.getStatus());
				submitLineRepository.save(line);
			}
			submit.updateStatus(dto.getStatus());
		}
		submitRepository.save(submit);
		return submitLine;
	}

	// 모든 결제 상대에게 결재 받으면 submit의 submitStatus상태 변경

}
