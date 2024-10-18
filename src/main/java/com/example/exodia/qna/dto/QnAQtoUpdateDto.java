package com.example.exodia.qna.dto;


import com.example.exodia.department.domain.Department;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnAQtoUpdateDto {
    private String questioner;
    private String questionText;
    private String title;
    private LocalDateTime updatedAt;
    private Department department;
    private List<MultipartFile> files;
}