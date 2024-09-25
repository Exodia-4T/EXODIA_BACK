package com.example.exodia.attendance.service;

import com.example.exodia.attendance.domain.Attendance;
import com.example.exodia.attendance.dto.AttendanceSaveDto;
import com.example.exodia.attendance.dto.AttendanceUpdateDto;
import com.example.exodia.attendance.dto.WeeklySumDto;
import com.example.exodia.attendance.repository.AttendanceRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    @Autowired
    private final AttendanceRepository attendanceRepository;
    @Autowired
    private final UserRepository userRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    /*출근시간 기록 용*/
    @Transactional
    public Attendance workIn(AttendanceSaveDto dto) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum).orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));

        Attendance attendance = dto.toEntity(user);
        return attendanceRepository.save(attendance);
    }

    /*퇴근시간 기록 용*/
    @Transactional
    public Attendance workOut(AttendanceUpdateDto dto) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum).orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));
        Attendance attendance = attendanceRepository.findTopByUserAndOutTimeIsNull(user).orElseThrow(() -> new RuntimeException("출근 기록이 존재하지 않습니다"));

        dto.updateEntity(attendance);
        return attendanceRepository.save(attendance);
    }

//    public WeeklySumDto getWeeklySum() {
//        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUserNum(userNum).orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));
//
//        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
//        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
//
//        List<Attendance> weeklyAttendance = attendanceRepository.findAllByUserAndWeek(user, startOfWeek, endOfWeek);
//        return calculateWeeklySum(weeklyAttendance);
//    }

//    public WeeklySumDto calculateWeeklySum(List<Attendance> weeklyAttendance) {
//        WeeklySumDto weeklySumDto = new WeeklySumDto();
//        double totalHours = 0;
//        double overallHours = 0;
//
//        for (Attendance attendance : weeklyAttendance) {
//            if (attendance.getInTime() != null && attendance.getOutTime() != null) {
//                double workHour = Duration.between(attendance.getInTime(), attendance.getOutTime()).toHours();
//                totalHours += workHour -1; // 점심시간 빼고 계산
//                if(workHour > 8) { // 8시간을 초과할경우 초과로 처리
//                    overallHours += (workHour - 8);
//                }
//            }
//        }
//        return new WeeklySumDto(totalHours, overallHours);
//    }

    // 주어진 기간의 주차별 근무 시간 합산 정보 조회
    @Transactional
    public List<WeeklySumDto> getWeeklySummaries(LocalDate startDate, LocalDate endDate) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum).orElseThrow(() -> new RuntimeException("존재하지 않는 사원입니다"));

        // 출퇴근 시간 데이터 가져오기
        List<Attendance> attendances = attendanceRepository.findAllByMemberAndInTimeBetween(user, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        // 주차별 근무 시간 합산
        Map<LocalDate, WeeklySumDto> weeklySummaryMap = new HashMap<>();

        for (Attendance attendance : attendances) {
            // 해당 출근 시간이 속한 주차(목요일 기준) 계산 * 국제 표준 ISO-8601 기준으로 계산
            LocalDate weekOfYear = WeekUtils.getWeekOfYear(attendance.getInTime().toLocalDate());
            // 해당 주차가 주차 맵에 없으면 새로 생성
            weeklySummaryMap.putIfAbsent(weekOfYear, new WeeklySumDto(0, 0, weekOfYear, weekOfYear.plusDays(6)));

            // 근무시간 + 초과시간 계산
            WeeklySumDto weeklySummary = weeklySummaryMap.get(weekOfYear);
            double hoursWorked = calculateWorkHours(attendance);
            weeklySummary.setTotalHours(weeklySummary.getTotalHours() + hoursWorked - 1);
            if (hoursWorked > 8) {
                weeklySummary.setOvertimeHours(weeklySummary.getOvertimeHours() + (hoursWorked - 8));
            }
        }

        return weeklySummaryMap.values().stream()
                .sorted(Comparator.comparing(WeeklySumDto::getStartOfWeek))
                .collect(Collectors.toList());
    }
    // 근무 시간 계산 (출근 시간과 퇴근 시간 차이)
    private double calculateWorkHours(Attendance attendance) {
        if (attendance.getInTime() != null && attendance.getOutTime() != null) {
            return Duration.between(attendance.getInTime(), attendance.getOutTime()).toHours();
        }
        return 0;
    }

}
