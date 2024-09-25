package com.example.exodia.reservation.controller;

import com.example.exodia.common.dto.CommonResDto;
import com.example.exodia.reservation.domain.Reservation;
import com.example.exodia.reservation.dto.ReservationCreateDto;
import com.example.exodia.reservation.dto.ReservationDto;
import com.example.exodia.reservation.service.ReservationService;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


//    public ResponseEntity<?> createReservation(@RequestBody ReservationCreateDto createDto) {
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약 취소가 완료되었습니다.", canceledReservation.getId());
//
//        ReservationDto createdReservation = reservationService.createReservation(createDto);
//        return ResponseEntity.ok(createdReservation);
//    }
//    @PostMapping("/car/create")
//    public ResponseEntity<?> reservationCreate(@RequestBody ReservationCreateDto dto) {
//        ReservationDto reservationDto = reservationService.carReservation(dto);
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "차량 예약 신청을 완료하였습니다.", reservationDto);
//        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
//    }
//
//
//
//    @GetMapping("/car/day")
//    public ResponseEntity<List<ReservationDto>> getReservationsForDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
//        List<ReservationDto> reservations = reservationService.getReservationsForDay(date);
//        return ResponseEntity.ok(reservations);
//    }
//
//    @GetMapping("/car/alllist")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<ReservationDto>> getAllReservations() {
//        List<ReservationDto> reservations = reservationService.getAllReservations();
//        return ResponseEntity.ok(reservations);
//    }
}
