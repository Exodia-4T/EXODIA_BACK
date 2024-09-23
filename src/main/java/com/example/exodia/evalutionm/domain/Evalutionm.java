package com.example.exodia.evalutionm.domain;


import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.subevalution.domain.SubEvalution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*중분류*/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Evalutionm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mName; // 중분류 명

    @ManyToOne
    @JoinColumn(name = "evalutionb_id", nullable = false)
    private Evalutionb evalutionb; // 대분류 상속

    @OneToOne(mappedBy = "evalutionm", cascade = CascadeType.ALL, orphanRemoval = true)
    private SubEvalution subEvalution;
}
