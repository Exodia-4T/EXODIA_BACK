package com.example.exodia.attendance.dto;

import com.example.exodia.attendance.domain.Attendance;
import com.example.exodia.attendance.domain.DayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDto {
    private Long id;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private DayStatus dayStatus;
    private Long userId;

    public static AttendanceDto fromEntity(Attendance attendance) {
        return AttendanceDto.builder()
                .id(attendance.getId())
                .inTime(attendance.getInTime())
                .outTime(attendance.getOutTime())
                .dayStatus(attendance.getDayStatus())
                .userId(attendance.getUser().getId())
                .build();
    }
}
