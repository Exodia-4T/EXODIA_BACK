package com.example.exodia.submit.dto;

import java.util.ArrayList;
import java.util.List;

import com.example.exodia.common.domain.DelYN;
import com.example.exodia.submit.domain.Submit;
import com.example.exodia.submit.domain.SubmitLine;
import com.example.exodia.submit.domain.SubmitStatus;
import com.example.exodia.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitSaveReqDto {

	private String submitType;
	private String contents;

	private List<SubmitUserDto> submitUserDtos;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SubmitUserDto {
		private String userName;
		private String position;
	}


	public Submit toEntity(User user) {
		return Submit.builder()
			.submitStatus(SubmitStatus.WAITING)
			.submitType(this.getSubmitType())
			.contents(this.getContents())
			.user(user)
			.submitLines(new ArrayList<>())
			.department_id(user.getDepartment().getId())
			.delYn(DelYN.N)
			.build();
	}

	public SubmitLine toLineEntity(User user) {
		return SubmitLine.builder()
			.submitStatus(SubmitStatus.WAITING)
			.userNum(user.getUserNum())
			.department_id(user.getDepartment().getId())
			.delYn(DelYN.N)
			.build();
	}

}
