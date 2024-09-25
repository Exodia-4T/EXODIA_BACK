package com.example.exodia.subevalution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubEvalutionUpdateDto {
    private Long id; // subEvalutionId
    private String content;
}
