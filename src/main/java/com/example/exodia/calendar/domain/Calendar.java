package com.example.exodia.calendar.domain;

import com.example.exodia.calendar.dto.CalendarSaveDto;
import com.example.exodia.calendar.dto.CalendarUpdateDto;
import com.example.exodia.user.domain.User;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "del_yn = 'N'")
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String content;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String type;
    @Column(name = "del_yn", nullable = false)
    private String delYn = "N";

    @Column(name = "google_event_id")
    private String googleEventId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static Calendar fromDto(CalendarSaveDto dto, User user) {
        return Calendar.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .startTime(LocalDateTime.parse(dto.getStartTime()))
                .endTime(LocalDateTime.parse(dto.getEndTime()))
                .type(dto.getType())
                .delYn("N")
                .user(user)
                .build();
    }

    public void updateFromDto(CalendarUpdateDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.startTime = LocalDateTime.parse(dto.getStartTime());
        this.endTime = LocalDateTime.parse(dto.getEndTime());
        this.type = dto.getType();
    }
}