package com.example.exodia.evalutionm.service;

import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.evalutionb.repository.EvalutionbRepository;
import com.example.exodia.evalutionm.domain.Evalutionm;
import com.example.exodia.evalutionm.dto.EvalutionmDto;
import com.example.exodia.evalutionm.repository.EvalutionmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvalutionmService {
    @Autowired
    private final EvalutionmRepository evalutionmRepository;
    @Autowired
    private final EvalutionbRepository evalutionbRepository;

    public EvalutionmService(EvalutionmRepository evalutionmRepository, EvalutionbRepository evalutionbRepository) {
        this.evalutionmRepository = evalutionmRepository;
        this.evalutionbRepository = evalutionbRepository;
    }

    /*중분류 생성*/
    public Evalutionm createEvalutionm(EvalutionmDto evalutionmDto) {
        Evalutionb evalutionb = evalutionbRepository.findById(evalutionmDto.getEvalutionbId())
                .orElseThrow(() -> new IllegalArgumentException("없는 대분류 아이디 : " + evalutionmDto.getEvalutionbId()));

        Evalutionm evalutionm = evalutionmDto.toEntity(evalutionb);

        return evalutionmRepository.save(evalutionm);
    }
    /*중분류 리스트*/
    public List<Evalutionm> getAllEvalutionms() {
        return evalutionmRepository.findAll();
    }
}
