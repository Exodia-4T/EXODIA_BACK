package com.example.exodia.car.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String carNum; // 차량 번호

    @Column(nullable = false)
    private String carType; // 차량 타입

    @Column(nullable = false)
    private int seatingCapacity; // 차량 인승

    @Column(nullable = false)
    private double engineDisplacement; // 베기량 (cc)

    @Column(nullable = false)
    private String carImage;

}
