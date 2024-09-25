package com.example.exodia.reservation.domain;

import com.example.exodia.car.domain.Car;
import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car; // 예약된 차량

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 예약한 사용자

    @Column(nullable = false)
    private LocalDateTime startTime; // 예약 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 예약 종료 시간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 예약 상태
}
