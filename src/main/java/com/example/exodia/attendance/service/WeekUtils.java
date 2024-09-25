package com.example.exodia.attendance.service;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class WeekUtils {
    // 주어진 날짜가 속한 주차를 계산하는 메서드 (목요일 기준)
    // * 국제 표준 ISO-8601 기준으로 계산
    public static LocalDate getWeekOfYear(LocalDate date) {
        // 해당 주의 목요일 찾기
        /*
        * 이유 ) 매월의 첫 주는 과반수(4일 이상)가 포함된 주를 기준
        * 1주일은 7일이므로 과반수라면 월요일, 화요일, 수요일, 목요일이 들어있거나 목요일, 금요일, 토요일, 일요일이 들어있는 주
        * 즉,주를 세는 기준은 월요일이 아닌 목요일 기준
        */

        LocalDate thursday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
        return thursday;
    }
    // 특정 날짜가 속한 달의 첫 번째 주차의 시작 날짜 계산
    public static LocalDate getFirstMondayOfMonth(int year, int month) {
        return LocalDate.of(year, month, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }
}

