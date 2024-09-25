package com.example.exodia.holiday.domain;

import com.example.exodia.hoildaycat.domain.HoildayCat;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "del_yn = 'N'")
public class Hoilday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private float userHoliday;

    @Column(nullable = false)
    private LocalDateTime hoildayStart;

    @Column(nullable = false)
    private LocalDateTime hoildayEnd;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "holidayCat_id", nullable = false)
    private HoildayCat hoildayCat;
}
