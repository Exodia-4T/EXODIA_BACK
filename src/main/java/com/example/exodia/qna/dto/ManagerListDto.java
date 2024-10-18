package com.example.exodia.qna.dto;

import com.example.exodia.qna.domain.Manager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerListDto {
    private Long managerId;
    private String userNum;
    private Long departmentId;
    private Long positionId;
    private String name;

    // Manager 엔티티로부터 DTO를 생성하는 메서드
    public static ManagerListDto fromEntity(Manager manager) {
        return new ManagerListDto(
                manager.getId(),
                manager.getUser().getUserNum(),
                manager.getUser().getDepartment().getId(),
                manager.getUser().getPosition().getId(),
                manager.getUser().getName()
        );
    }
}
