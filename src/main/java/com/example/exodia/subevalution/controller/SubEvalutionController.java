package com.example.exodia.subevalution.controller;

import com.example.exodia.subevalution.domain.SubEvalution;
import com.example.exodia.subevalution.dto.SubEvalutionDto;
import com.example.exodia.subevalution.dto.SubEvalutionUpdateDto;
import com.example.exodia.subevalution.service.SubEvalutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sub-evalution")
public class SubEvalutionController {
    private final SubEvalutionService subEvalutionService;

    public SubEvalutionController(SubEvalutionService subEvalutionService) {
        this.subEvalutionService = subEvalutionService;
    }

    @PostMapping("/create")
    public ResponseEntity<SubEvalution> createSubEvalution(@RequestBody SubEvalutionDto subEvalutionDto) {
        SubEvalution createdSubEvalution = subEvalutionService.createSubEvalution(subEvalutionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubEvalution);
    }

    // 소분류 조회 (로그인한 사용자가 작성한 모든 소분류)
    @GetMapping("/list")
    public ResponseEntity<List<SubEvalution>> getAllSubEvalutionsForUser() {
        List<SubEvalution> subEvalutions = subEvalutionService.getAllSubEvalutionsForUser();
        return ResponseEntity.ok(subEvalutions);
    }

    /* 소분류 수정 */
    @PutMapping("/update/{id}")
    public ResponseEntity<SubEvalution> updateSubEvalution(@PathVariable Long id, @RequestBody SubEvalutionUpdateDto subEvalutionUpdateDto) {
        subEvalutionUpdateDto.setId(id);
        SubEvalution updatedSubEvalution = subEvalutionService.updateSubEvalution(subEvalutionUpdateDto);
        return ResponseEntity.ok(updatedSubEvalution);
    }

    /* 소분류 삭제 */
    //  소분류의 경우 일정 기간이 지나면 사라지는 휘발성 데이터를 지향, 따로 소프트 del 적용x
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubEvalution(@PathVariable Long id) {
        subEvalutionService.deleteSubEvalution(id);
        return ResponseEntity.noContent().build();
    }
}
