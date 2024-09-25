package com.example.exodia.calendar.dto;

import com.example.exodia.calendar.domain.Calendar;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class CalendarSaveDto {
    private String title;
    private String content;
    private String startTime;
    private String endTime;
    private String type;
    private Long userId;
    private String delYn = "N";

    private LocalDateTime parseToLocalDateTime(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }

    public Calendar toEntity(User user) {
        return Calendar.builder()
                .title(this.title)
                .content(this.content)
                .startTime(LocalDateTime.parse(this.startTime))
                .endTime(LocalDateTime.parse(this.endTime))
                .type(this.type)
                .user(user)
                .delYn(this.delYn)
                .build();
    }
}