package com.example.exodia.reservationVehicle.service;

import com.example.exodia.car.domain.Car;
import com.example.exodia.car.repository.CarRepository;
import com.example.exodia.common.auth.JwtTokenProvider;
import com.example.exodia.reservationVehicle.domain.Reservation;
import com.example.exodia.reservationVehicle.domain.Status;
import com.example.exodia.reservationVehicle.dto.ReservationCreateDto;
import com.example.exodia.reservationVehicle.dto.ReservationDto;
import com.example.exodia.reservationVehicle.repository.ReservationRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.user.service.UserService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedissonClient redissonClient; // Redisson 클라이언트 추가

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    // 차량 예약 메서드
    // 차량 예약 메서드
    public ReservationDto carReservation(ReservationCreateDto dto) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        RLock lock = redissonClient.getLock("carReservationLock:" + dto.getCarId() + ":" + dto.getStartDate());

        try {
            if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                Car car = carRepository.findById(dto.getCarId())
                        .orElseThrow(() -> new IllegalArgumentException("차량이 존재하지 않습니다."));
                User user = userRepository.findByUserNum(userNum)
                        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

                // 특정 날짜에 예약된 모든 예약을 가져옴
                List<Reservation> existingReservations = reservationRepository.findByCarIdAndDate(
                        car.getId(), dto.getStartDate().atStartOfDay(), dto.getStartDate().atTime(LocalTime.MAX));

                // 예약 가능한지 확인
                boolean canReserve = existingReservations.stream().allMatch(Reservation::canReserve);
                if (!canReserve) {
                    throw new IllegalArgumentException("해당 날짜에 차량이 이미 예약되어 있거나 예약 가능한 상태가 아닙니다.");
                }

                // 예약 생성 및 상태를 WAITING으로 설정
                Reservation reservation = dto.toEntity(car, user);
                reservation.setStatus(Status.WAITING);
                Reservation savedReservation = reservationRepository.save(reservation);

                return ReservationDto.fromEntity(savedReservation);
            } else {
                throw new IllegalArgumentException("현재 다른 사용자가 예약을 진행 중입니다. 잠시 후 다시 시도해주세요.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("예약 중 문제가 발생했습니다. 다시 시도해주세요.");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /* 예약 승인 */
    public ReservationDto approveReservation(Long reservationId) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        userService.checkHrAuthority(user.getDepartment().getId().toString());
        // 예약 상태를 APPROVED로 변경
        reservation.approveReservation();
        reservation.reserve(); // 최종 예약 확정 상태로 변경
        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationDto.fromEntity(updatedReservation);
    }

    /* 예약 거절 */
    public ReservationDto rejectReservation(Long reservationId) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        userService.checkHrAuthority(user.getDepartment().getId().toString());

        // 예약 상태를 AVAILABLE로 변경
        reservation.rejectReservation();
        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationDto.fromEntity(updatedReservation);
    }



    // 특정 날짜에 차량이 예약 가능한지 확인 메서드
    public boolean isCarAvailableForDate(Long carId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<Reservation> reservations = reservationRepository.findByCarIdAndDate(carId, startOfDay, endOfDay);

        // 예약이 없거나 모든 예약이 취소된 경우 예약 가능
        return reservations.stream().allMatch(reservation -> reservation.getStatus() == Status.REJECTED);
    }

    // 특정 날짜의 예약 조회 메서드
    public List<ReservationDto> getReservationsForDay(LocalDateTime date) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return reservationRepository.findByStartTimeBetween(date, date.plusDays(1))
                .stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 모든 예약 조회 메서드
    public List<ReservationDto> getAllReservations() {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        userService.checkHrAuthority(user.getDepartment().getId().toString());

        return reservationRepository.findAll().stream()
                .map(ReservationDto::fromEntity)
                .collect(Collectors.toList());
    }
}


