package com.example.exodia.attendance.repository;

import com.example.exodia.attendance.domain.Attendance;
import com.example.exodia.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("SELECT a FROM Attendance a WHERE a.user = :user AND a.inTime >= :startOfWeek AND a.inTime < :endOfWeek")
    List<Attendance> findAllByUserAndWeek(@Param("user") User user, @Param("startOfWeek")LocalDateTime startOfWeek, @Param("endOfWeek")LocalDateTime endOfWeek);

    @Query("SELECT a FROM Attendance a WHERE a.user = :user AND a.inTime BETWEEN :startDate AND :endDate")
    List<Attendance> findAllByMemberAndInTimeBetween(@Param("user") User user,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);
    Optional<Attendance> findTopByUserAndOutTimeIsNull(User user);
}
