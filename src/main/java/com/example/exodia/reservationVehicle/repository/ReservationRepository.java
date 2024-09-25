package com.example.exodia.reservationVehicle.repository;

import com.example.exodia.reservationVehicle.domain.Reservation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 날짜에 해당 차량이 예약되어 있는지 확인
    @Query("SELECT r FROM Reservation r WHERE r.car.id = :carId AND r.startTime < :endOfDay AND r.endTime > :startOfDay")
    List<Reservation> findByCarIdAndDate(@Param("carId") Long carId,
                                         @Param("startOfDay") LocalDateTime startOfDay,
                                         @Param("endOfDay") LocalDateTime endOfDay);

    // 특정 날짜에 예약된 항목을 찾기 위한 메서드
    List<Reservation> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}








