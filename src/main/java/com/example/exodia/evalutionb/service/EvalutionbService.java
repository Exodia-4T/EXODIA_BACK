package com.example.exodia.evalutionb.service;

import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.evalutionb.dto.EvalutionbDto;
import com.example.exodia.evalutionb.repository.EvalutionbRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvalutionbService {
    @Autowired
    private final EvalutionbRepository evalutionbRepository;

    public EvalutionbService(EvalutionbRepository evalutionbRepository) {
        this.evalutionbRepository = evalutionbRepository;
    }
    /*대분류 생성*/
    public Evalutionb createEvalutionb(EvalutionbDto evalutionbDto) {

        Evalutionb evalutionb = new Evalutionb();
        evalutionb.setBName(evalutionbDto.getBName());

        return evalutionbRepository.save(evalutionb);
    }

    /*대분류 리스트*/
    public List<Evalutionb> getAllEvalutionbs() {
        return evalutionbRepository.findAll();
    }
}