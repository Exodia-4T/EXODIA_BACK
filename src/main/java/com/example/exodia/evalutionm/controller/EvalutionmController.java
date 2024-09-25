package com.example.exodia.evalutionm.controller;

import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.evalutionm.domain.Evalutionm;
import com.example.exodia.evalutionm.dto.EvalutionmDto;
import com.example.exodia.evalutionm.service.EvalutionmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evalutionm")
public class EvalutionmController {
    @Autowired
    private final EvalutionmService evalutionmService;

    public EvalutionmController(EvalutionmService evalutionmService) {
        this.evalutionmService = evalutionmService;
    }

    @PostMapping("/create")
    public ResponseEntity<Evalutionm> createEvalutionm(@RequestBody EvalutionmDto evalutionmDto) {
        Evalutionm createdEvalutionm = evalutionmService.createEvalutionm(evalutionmDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvalutionm);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Evalutionm>> getAllEvalutionms() {
        List<Evalutionm> evalutionms = evalutionmService.getAllEvalutionms();
        return ResponseEntity.ok(evalutionms);
    }
}
