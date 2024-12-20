package com.example.exodia.reservationVehicle.controller;

import com.example.exodia.common.dto.CommonResDto;
import com.example.exodia.reservationVehicle.dto.CarReservationStatusDto;
import com.example.exodia.reservationVehicle.dto.ReservationCreateDto;
import com.example.exodia.reservationVehicle.dto.ReservationDto;
import com.example.exodia.reservationVehicle.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


    /* 차량 예약 생성 API */
    @PostMapping("/car/create")
    //@Operation(summary= "[일반 사용자] 차량 예약 생성 API")
    public ResponseEntity<CommonResDto> reservationCreate(@RequestBody ReservationCreateDto dto) {
        ReservationDto reservationDto = reservationService.carReservation(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "차량 예약 신청을 완료하였습니다.", reservationDto);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    // 예약 승인 API (관리자만 접근 가능)
    @PutMapping("/car/approve/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    //@Operation(summary= "[관리자 사용자] 차량 예약 승인 API")
    public ResponseEntity<CommonResDto> approveReservation(@PathVariable Long reservationId) {
        ReservationDto reservationDto = reservationService.approveReservation(reservationId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약이 승인되었습니다.", reservationDto);
        return ResponseEntity.ok(commonResDto);
    }

    // 예약 거절 API (관리자만 접근 가능)
    @DeleteMapping("/car/reject/{reservationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResDto> rejectReservation(@PathVariable Long reservationId) {
        // 예약 삭제 처리
        reservationService.rejectReservation(reservationId);
        // 삭제 성공 메시지 반환
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "예약이 거절되고 삭제되었습니다.", reservationId);
        return ResponseEntity.ok(commonResDto);
    }

    /* 특정 날짜의 예약 조회 API */
    @GetMapping("/car/day")
    //@Operation(summary= "[일반 사용자] 입력일 차량 예약 조회 API")
    public ResponseEntity<List<ReservationDto>> getReservationsForDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ReservationDto> reservations = reservationService.getReservationsForDay(date.atStartOfDay());
        return ResponseEntity.ok(reservations);
    }

    // 모든 예약 조회 API (관리자 권한 필요)
    @GetMapping("/car/alllist")
    @PreAuthorize("hasRole('ADMIN')")
    //@Operation(summary= "[관리자 사용자] 차량 예약 조회 API")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/allcar/day")
    public ResponseEntity<List<CarReservationStatusDto>> getReservationsForDayAllCar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CarReservationStatusDto> carReservationStatusList = reservationService.getAllCarsWithReservationStatusForDay(date.atStartOfDay());
        return ResponseEntity.ok(carReservationStatusList);
    }



}
