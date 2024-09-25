package com.example.exodia.holiday.repository;

import com.example.exodia.holiday.domain.Hoilday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Hoilday, Long> {
}
