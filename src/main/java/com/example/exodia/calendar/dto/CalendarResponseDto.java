package com.example.exodia.calendar.dto;

import com.example.exodia.calendar.domain.Calendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String type;
    private String userName;
    private String googleEventId;

    public static CalendarResponseDto fromEntity(Calendar calendar) {
        return CalendarResponseDto.builder()
                .id(calendar.getId())
                .title(calendar.getTitle())
                .content(calendar.getContent())
                .startTime(calendar.getStartTime())
                .endTime(calendar.getEndTime())
                .type(calendar.getType())
                .userName(calendar.getUser().getName())
                .googleEventId(calendar.getGoogleEventId())
                .build();
    }
}