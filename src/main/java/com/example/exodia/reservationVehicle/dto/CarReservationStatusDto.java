package com.example.exodia.reservationVehicle.dto;

import com.example.exodia.car.domain.Car;
import com.example.exodia.reservationVehicle.domain.Status;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarReservationStatusDto {
    private Long carId;
    private String carNum;
    private String carType;
    private Status status;
    private String userName;
    private String departmentName;

    public static CarReservationStatusDto fromEntity(Car car, Status status, User user) {
        return CarReservationStatusDto.builder()
                .carId(car.getId())
                .carNum(car.getCarNum())
                .carType(car.getCarType())
                .status(status)
                .userName(user != null ? user.getName() : null)
                .departmentName(user != null ? user.getDepartment().getName() : null)
                .build();
    }
}