package com.example.exodia.attendance.dto;

import com.example.exodia.attendance.domain.Attendance;
import com.example.exodia.attendance.domain.DayStatus;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/* 출근 기록 DTO */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSaveDto {
    private LocalDateTime inTime;

    public Attendance toEntity(User user) {
        return Attendance.builder()
                .user(user)
                .inTime(this.inTime != null ? this.inTime : LocalDateTime.now())
                .dayStatus(DayStatus.O)
                .build();
    }

}
