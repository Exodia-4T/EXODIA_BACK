package com.example.exodia.reservation.dto;

import com.example.exodia.car.domain.Car;
import com.example.exodia.reservation.domain.Reservation;
import com.example.exodia.reservation.domain.Status;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateDto {

    private Long carId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Reservation toEntity(Car car, User user) {
        return Reservation.builder()
                .car(car)
                .user(user)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .status(Status.RESERVED)
                .build();
    }
}
