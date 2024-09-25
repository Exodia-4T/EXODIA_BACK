package com.example.exodia.evalutionm.dto;

import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.evalutionm.domain.Evalutionm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalutionmDto {
    private Long evalutionbId;
    private String mName;

    public Evalutionm toEntity(Evalutionb evalutionb) {
        return Evalutionm.builder()
                .mName(this.mName)
                .evalutionb(evalutionb)
                .build();
    }
}
