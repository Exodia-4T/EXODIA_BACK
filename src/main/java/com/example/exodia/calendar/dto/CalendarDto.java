package com.example.exodia.calendar.dto;

import com.example.exodia.calendar.domain.Calendar;
import com.example.exodia.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDto {
    private Long id;
    private String title;
    private String content;
    private String startTime;
    private String endTime;
    private String type;
    private String userName;
    private String googleEventId;

    // LocalDateTime -> DateTimeFormatter 으로 반환
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    public static CalendarDto fromEntity(Calendar calendar) {
        return CalendarDto.builder()
                .id(calendar.getId())
                .title(calendar.getTitle())
                .content(calendar.getContent())
                .startTime(calendar.getStartTime().format(formatter))
                .endTime(calendar.getEndTime().format(formatter))
                .type(calendar.getType())
                .userName(calendar.getUser().getName())
                .googleEventId(calendar.getGoogleEventId())
                .build();
    }
}