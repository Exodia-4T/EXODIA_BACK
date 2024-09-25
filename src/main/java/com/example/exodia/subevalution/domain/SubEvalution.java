package com.example.exodia.subevalution.domain;

import com.example.exodia.evalution.domain.Evalution;
import com.example.exodia.evalutionm.domain.Evalutionm;
import com.example.exodia.subevalution.dto.SubEvalutionDto;
import com.example.exodia.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SubEvalution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content; // 작성한 분류 명


    // 재귀 호출에 의해서 중분류와 무환 순환 구조 발생으로 jsonIgnore 조치
    // 다른 부분에서 오류 시 Evalutionm 에서 sub-Evalutation 삭제
    @OneToOne
    @JoinColumn(name = "evalutionm_id", nullable = false)
    @JsonIgnore
    private Evalutionm evalutionm; // 중분류 상속

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "subEvalution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evalution> evalutions = new ArrayList<>();

//    public static SubEvalution toEntity(SubEvalutionDto dto, Evalutionm evalutionm, User user) {
//        return SubEvalution.builder()
//                .content(dto.getContent())
//                .evalutionm(evalutionm)
//                .user(user)
//                .build();
//    }
}
