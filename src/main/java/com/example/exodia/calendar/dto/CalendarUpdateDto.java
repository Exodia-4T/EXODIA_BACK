package com.example.exodia.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarUpdateDto {
    private String title;
    private String content;
    private String startTime;
    private String endTime;
    private String type;
}