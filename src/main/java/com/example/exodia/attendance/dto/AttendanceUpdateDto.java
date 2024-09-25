package com.example.exodia.attendance.dto;

import com.example.exodia.attendance.domain.Attendance;
import com.example.exodia.attendance.domain.DayStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/* 출근 기록 DTO */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUpdateDto {
    private LocalDateTime outTime;

    public void updateEntity(Attendance attendance) {
        attendance.setOutTime(this.outTime != null ? this.outTime : LocalDateTime.now());
        attendance.setDayStatus(DayStatus.X);
    }
}
