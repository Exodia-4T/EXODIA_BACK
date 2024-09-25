package com.example.exodia.subevalution.dto;

import com.example.exodia.evalutionm.domain.Evalutionm;
import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubEvalutionDto {
    private Long evalutionmId;
    private String content;

    public SubEvalution toEntity(Evalutionm evalutionm, User user) {
        return SubEvalution.builder()
                .content(this.content)
                .evalutionm(evalutionm)
                .user(user)
                .build();
    }
}
