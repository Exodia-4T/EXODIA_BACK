package com.example.exodia.calendar.controller;


import com.example.exodia.calendar.dto.CalendarResponseDto;
import com.example.exodia.calendar.dto.CalendarSaveDto;
import com.example.exodia.calendar.dto.CalendarUpdateDto;
import com.example.exodia.calendar.service.CalendarService;
import com.example.exodia.common.dto.CommonResDto;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/calendars")
public class CalendarController {

    @Autowired
    private final CalendarService calendarService;
    @Autowired
    private final UserRepository userRepository;

    public CalendarController(CalendarService calendarService, UserRepository userRepository) {
        this.calendarService = calendarService;
        this.userRepository = userRepository;
    }


    /* 이벤트 생성 */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    //@Operation(summary= "[일반 사용자] 캘린더 이벤트 생성 API")
    public ResponseEntity<?> createCalendarEvent(@RequestBody CalendarSaveDto dto) throws Exception {
        CalendarResponseDto responseDto = calendarService.createCalendarEvent(dto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED, "캘린더 이벤트 생성이 완료되었습니다.", responseDto), HttpStatus.CREATED);
    }

    /* 이벤트 업데이트(수정) */
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    //@Operation(summary= "[일반 사용자] 캘린더 이벤트 수정 API")
    public ResponseEntity<?> updateCalendarEvent(@PathVariable Long id, @RequestBody CalendarUpdateDto dto) throws Exception {
        CalendarResponseDto responseDto = calendarService.updateCalendarEvent(id, dto);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "캘린더 이벤트 수정이 완료되었습니다.", responseDto), HttpStatus.OK);
    }
    /* 이벤트 삭제 */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    //@Operation(summary= "[일반 사용자] 캘린더 이벤트 삭제 API")
    public ResponseEntity<?> deleteCalendarEvent(@PathVariable Long id) throws Exception {
        calendarService.deleteCalendarEvent(id);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.NO_CONTENT, "캘린더 이벤트 삭제가 완료되었습니다.", null), HttpStatus.NO_CONTENT);
    }

    /* 공휴일 정보 + 사용자 생성 리스트 */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/allevents")
    public ResponseEntity<?> getUserAndHolidayCalendars() {
        try {
            List<CalendarResponseDto> responseDtos = calendarService.getUserAndHolidayCalendars();
            return new ResponseEntity<>(responseDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /* 캘린더 리스트 */
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/list")
//    //@Operation(summary= "[일반 사용자] 캘린더 이벤트 목록 조회 API")
//    public ResponseEntity<?> getUserCalendars() {
//        List<CalendarResponseDto> responseDtos = calendarService.getUserCalendars();
//        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK, "캘린더 목록 조회가 완료되었습니다.", responseDtos), HttpStatus.OK);
//    }
}