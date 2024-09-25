package com.example.exodia.reservationVehicle.domain;

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
    private Status status = Status.AVAILABLE; // 예약 상태, 기본값은 예약 가능한 AVAILABLE

    // WAITING -> APPROVE
    public void approveReservation() {
        this.status = Status.APPROVED;
    }

    // WAITING -> REJECT -> AVAILALBE
    public void rejectReservation() {
        this.status = Status.AVAILABLE;
    }

    // APPROVED -> RESERVED
    public void reserve() {
        this.status = Status.RESERVED;
    }

    // 예약 가능 여부
    public boolean canReserve() {
        return this.status == Status.WAITING || this.status == Status.REJECTED || this.status == Status.AVAILABLE;
    }
}
