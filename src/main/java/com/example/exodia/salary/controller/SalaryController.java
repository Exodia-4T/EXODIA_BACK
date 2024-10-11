// SalaryController.java

package com.example.exodia.salary.controller;

import com.example.exodia.salary.domain.Salary;
import com.example.exodia.salary.dto.SalaryDto;
import com.example.exodia.salary.service.SalaryService;
import com.example.exodia.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    // 사원의 월급 명세서 조회
    @GetMapping("/my")
    public ResponseEntity<SalaryDto> getMySalary(User user) {
        Salary salary = salaryService.getSalarySlip(user);
        int yearsOfService = salaryService.calculateYearsOfService(user);  // 입사년차 계산
        return ResponseEntity.ok(SalaryDto.fromEntity(salary, yearsOfService));
    }

    // 상세 페이지를 위한 사원별 월급 명세서 조회
    @GetMapping("/detail/{userNum}")
    public ResponseEntity<SalaryDto> getSalaryDetail(@PathVariable String userNum) {
        Optional<Salary> salary = salaryService.getSalaryByUserNum(userNum);
        if (salary.isPresent()) {
            User user = salary.get().getUser();
            int yearsOfService = salaryService.calculateYearsOfService(user);  // 입사년차 계산
            return ResponseEntity.ok(SalaryDto.fromEntity(salary.get(), yearsOfService));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SalaryDto>> getAllSalaries() {
        List<Salary> salaries = salaryService.getAllSalaries();
        List<SalaryDto> salaryDtos = salaries.stream()
                .map(salary -> SalaryDto.fromEntity(salary, salaryService.calculateYearsOfService(salary.getUser())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(salaryDtos);
    }

    @GetMapping("/byPosition/{positionId}")
    public ResponseEntity<List<SalaryDto>> getSalariesByPosition(@PathVariable Long positionId) {
        List<Salary> salaries = salaryService.getSalariesByPosition(positionId);
        List<SalaryDto> salaryDtos = salaries.stream()
                .map(salary -> SalaryDto.fromEntity(salary, salaryService.calculateYearsOfService(salary.getUser())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(salaryDtos);
    }

}
