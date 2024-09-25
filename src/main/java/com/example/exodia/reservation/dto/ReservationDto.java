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
public class ReservationDto {

    private Long reservationId;
    private Long carId;
    private String carNum;
    private String carType;
    private String userName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;

    public static ReservationDto fromEntity(Reservation reservation) {
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .carId(reservation.getCar().getId())
                .carNum(reservation.getCar().getCarNum())
                .carType(reservation.getCar().getCarType())
                .userName(reservation.getUser().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .build();
    }
}

