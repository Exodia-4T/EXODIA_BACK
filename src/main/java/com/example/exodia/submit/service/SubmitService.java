package com.example.exodia.submit.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.exodia.common.service.KafkaProducer;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.exodia.position.domain.Position;
import com.example.exodia.position.repository.PositionRepository;
import com.example.exodia.submit.domain.Submit;
import com.example.exodia.submit.domain.SubmitLine;
import com.example.exodia.submit.domain.SubmitStatus;
import com.example.exodia.submit.domain.SubmitType;
import com.example.exodia.submit.dto.SubmitDetResDto;
import com.example.exodia.submit.dto.SubmitListResDto;
import com.example.exodia.submit.dto.SubmitSaveReqDto;
import com.example.exodia.submit.dto.SubmitStatusUpdateDto;
import com.example.exodia.submit.repository.SubmitLineRepository;
import com.example.exodia.submit.repository.SubmitRepository;
import com.example.exodia.submit.repository.SubmitTypeRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SubmitService {

	private final SubmitRepository submitRepository;
	private final UserRepository userRepository;
	private final PositionRepository positionRepository;
	private final SubmitLineRepository submitLineRepository;
	private final KafkaProducer kafkaProducer;
	private final SubmitTypeRepository submitTypeRepository;

	public SubmitService(SubmitRepository submitRepository, UserRepository userRepository,
		PositionRepository positionRepository, SubmitLineRepository submitLineRepository,
		SubmitTypeRepository submitTypeRepository, KafkaProducer kafkaProducer) {
		this.submitRepository = submitRepository;
		this.userRepository = userRepository;
		this.positionRepository = positionRepository;
		this.submitLineRepository = submitLineRepository;
		this.kafkaProducer = kafkaProducer;
		this.submitTypeRepository = submitTypeRepository;
	}

	// 	결재라인 등록
	@Transactional
	public Submit createSubmit(SubmitSaveReqDto dto) {
		String userNum = SecurityContextHolder.getContext().getAuthentication().getName();

		User submitUser = userRepository.findByUserNum(userNum)
			.orElseThrow(() -> new EntityNotFoundException("회원정보가 존재하지 않습니다."));

		Submit submit = dto.toEntity(submitUser);
		SubmitLine submitLine = SubmitLine.builder().build();

		// 결재라인
		for (SubmitSaveReqDto.SubmitUserDto userDto : dto.getSubmitUserDtos()) {
			Position position = positionRepository.findById(userDto.getPosition())
				.orElseThrow(() -> new EntityNotFoundException("직급 정보가 존재하지 않습니다."));

			User user = userRepository.findByNameAndPosition(userDto.getUserName(), position)
				.orElseThrow(() -> new EntityNotFoundException("회원정보가 존재하지 않습니다."));

			submitLine = dto.toLineEntity(user);

			submit.getSubmitLines().add(submitLine);
			submitLine.updateSubmit(submit);

			// Kafka 이벤트 전송
			String message = String.format("%s님에게 %s 결재 요청이 도착했습니다.", user.getName(), dto.getSubmitType());
			kafkaProducer.sendSubmitNotification("submit-events", user.getUserNum(), message);
		}

		submitRepository.save(submit);
		submitLineRepository.save(submitLine);

		return submit;
	}

	// 결재 상태 변경
	@Transactional
	public List<SubmitLine> updateSubmit(SubmitStatusUpdateDto dto) throws IOException, EntityNotFoundException {
		String userNum = SecurityContextHolder.getContext().getAuthentication().getName();    // 사용자
		// 이전 결재자들의 결재 상태를 확인하기 위해 필요
		List<SubmitLine> submitLines = submitLineRepository.findBySubmitIdOrderByUserPositionId(
			dto.getSubmitId());    // 직급 순서대로 가져오는걸로
		Submit submit = submitRepository.findById(dto.getSubmitId())
			.orElseThrow(() -> new EntityNotFoundException("결재 정보가 존재하지 않습니다."));

		// 나의 결재 상태를 확인하기 위해서 필요

		// 1. 내가 이미 처리한 결재인 경우
		SubmitLine mySubmitLine = submitLineRepository.findBySubmitIdAndUserNum(dto.getSubmitId(), userNum);
		if (mySubmitLine.getSubmitStatus() != SubmitStatus.WAITING) {
			throw new IOException("이미 처리 된 결재입니다.");
		}

		int idx = 0;
		for (SubmitLine submitLine : submitLines) {
			if (!submitLine.getUserNum().equals(userNum)) {
				// 2. 이전 결재자의 결재가 필요한 경우
				if (submitLine.getSubmitStatus() == SubmitStatus.WAITING) {
					throw new IOException("이전 결재자의 결재가 필요합니다.");
				}
			} else {
				// REJECT
				if (dto.getStatus() == SubmitStatus.REJECT) {
					if (dto.getReason() == null) {
						throw new IOException("반려 사유를 입력해주세요.");
					} else {
						// 	submitLine, submit 모든걸 reject로
						changeToReject(dto.getSubmitId(), dto.getReason());
					}
				} else {
					// 	ACCEPT
					// 	subLine상태 바꾸기
					// 	내가 최상단 결재자라면 submit상태도 바꾸기
					submitLine.updateStatus(SubmitStatus.ACCEPT);
					if (idx == submitLines.size() - 1) {
						submit.updateStatus(SubmitStatus.ACCEPT,null);
					}
				}
			}


			idx++;
		}
		return submitLines;
	}

	@Transactional
	public void changeToReject(Long submitId, String reason) {
		// 히스토리 모든 문서들 REJECT
		List<SubmitLine> submitLines = submitLineRepository.findBySubmitIdOrderByUserNumDesc(submitId);
		for (SubmitLine line : submitLines) {
			line.updateStatus(SubmitStatus.REJECT);
		}
		Submit submit = submitRepository.findById(submitId)
			.orElseThrow(() -> new EntityNotFoundException("결재 정보가 존재하지 않습니다."));
		submit.updateStatus(SubmitStatus.REJECT, reason);
	}

	// 결재 타입 리스트 전체 조회
	public List<?> getTypeList() {
		List<SubmitType> types = submitTypeRepository.findAll();
		return types.stream()
			.map(SubmitType::getTypeName)
			.collect(Collectors.toList());
	}

	// 나에게 요청 들어온 결재 리스트 조회
	public List<?> getSubmitList() {
		String userNum = SecurityContextHolder.getContext().getAuthentication().getName();

		List<SubmitLine> submitLines = submitLineRepository.findAllByUserNumOrderByCreatedAtDesc(userNum);
		List<SubmitListResDto> mySubmitList = submitLines.stream()
			.map(submitLine -> {
				User user = submitLine.getSubmit().getUser();
				return new SubmitListResDto().fromLineEntity(user, submitLine);
			})
			.collect(Collectors.toList());
		return mySubmitList;
	}

	// 내가 요청한 결재 리스트 조회
	public List<?> getMySubmitList() {
		String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUserNum(userNum)
			.orElseThrow(() -> new EntityNotFoundException("회원 정보가 존재하지 않습니다."));

		List<Submit> submits = submitRepository.findAllByUserOrderByCreatedAtDesc(user);

		List<SubmitListResDto> mySubmitList = submits.stream()
			.map(submit -> {
				return new SubmitListResDto().fromEntity(submit);
			})
			.collect(Collectors.toList());
		return mySubmitList;
	}

	public SubmitDetResDto getSubmitDetail(Long id) {
		Submit submit = submitRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("결재 정보가 존재하지 않습니다."));
		return submit.fromEntity();
	}

}
