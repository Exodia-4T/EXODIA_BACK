package com.example.exodia.evalutionb.controller;

import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.evalutionb.dto.EvalutionbDto;
import com.example.exodia.evalutionb.service.EvalutionbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* 이 부분은 만들 때부터 db 상으로 대 - 중 분류는 완성이 되어 있기 때문에 따로 토큰 설정을 하지 않음*/
/*  추 후 필요 시 인사팀에서 수정 가능하게 jwt 토큰 수정 필요*/
@RestController
@Slf4j
@RequestMapping("/evalutionb")
public class EvalutionbController {
    @Autowired
    private final EvalutionbService evalutionbService;

    public EvalutionbController(EvalutionbService evalutionbService) {
        this.evalutionbService = evalutionbService;
    }


    @PostMapping("/create")
    public ResponseEntity<Evalutionb> createEvalutionb(@RequestBody EvalutionbDto evalutionbDto) {
        Evalutionb createdEvalutionb = evalutionbService.createEvalutionb(evalutionbDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvalutionb);
    }

    // 대분류 조회 엔드포인트 (옵션)
    @GetMapping("/list")
    public ResponseEntity<List<Evalutionb>> getAllEvalutionbs() {
        List<Evalutionb> evalutionbs = evalutionbService.getAllEvalutionbs();
        return ResponseEntity.ok(evalutionbs);
    }
}