package com.example.exodia.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/* 주차별 기록 DTO */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklySumDto {
    private double totalHours;     // 주간 근무 시간 총합
    private double overtimeHours;  // 주간 초과 근무 시간
    private LocalDate startOfWeek; // 주 시작 날짜
    private LocalDate endOfWeek;   // 주 끝 날짜

    // 주차 범위 설정
    public void setWeekRange(LocalDate startOfWeek, LocalDate endOfWeek) {
        this.startOfWeek = startOfWeek;
        this.endOfWeek = endOfWeek;
    }
}
