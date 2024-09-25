package com.example.exodia.reservation.service;

import com.example.exodia.car.domain.Car;
import com.example.exodia.car.repository.CarRepository;
import com.example.exodia.reservation.domain.Reservation;
import com.example.exodia.reservation.dto.ReservationCreateDto;
import com.example.exodia.reservation.dto.ReservationDto;
import com.example.exodia.reservation.repository.ReservationRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;

//    public ReservationDto carReservation(ReservationCreateDto dto) {
//        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        Car car = carRepository.findById(dto.getCarId())
//                .orElseThrow(() -> new IllegalArgumentException("차량이 존재하지 않습니다."));
//        User user = userRepository.findByUserNum(userNum)
//                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
//
//        if (!isCarAvailable(car.getId(), dto.getStartTime(), dto.getEndTime())) {
//            throw new IllegalArgumentException("해당 시간에 차량이 예약되어 있습니다.");
//        }
//
//        Reservation reservation = dto.toEntity(car, user);
//        Reservation savedReservation = reservationRepository.save(reservation);
//
//        return ReservationDto.fromEntity(savedReservation);
//    }
//
//    public boolean isCarAvailable(Long carId, LocalDateTime startTime, LocalDateTime endTime) {
////        List<Reservation> reservations = reservationRepository.findByCarIdAndStartTimeLessThanAndEndTimeGreaterThan(carId, endTime, startTime);
//        return reservations.isEmpty(); // 예약이 겹치는 경우가 없을 때만 예약 가능
//    }
//
//
//    public List<ReservationDto> getAllReservations() {
//        return reservationRepository.findAll().stream().map(ReservationDto::fromEntity)
//                .collect(Collectors.toList());
//    }
}


