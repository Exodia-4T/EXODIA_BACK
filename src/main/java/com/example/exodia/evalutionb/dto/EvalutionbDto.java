package com.example.exodia.evalutionb.dto;

import com.example.exodia.evalutionb.domain.Evalutionb;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalutionbDto {
    private String bName; // 대분류 명

    public void setBName(String bName) {
        this.bName = bName;
    }
}
