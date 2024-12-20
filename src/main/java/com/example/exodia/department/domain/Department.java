package com.example.exodia.department.domain;

import com.example.exodia.common.domain.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Department parentDepartment;

    @Column(length = 3000)
    private String description;

    @OneToMany(mappedBy = "parentDepartment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Department> children = new ArrayList<>();

    public Department(String name, Department parentDepartment) {
        this.name = name;
        this.parentDepartment = parentDepartment;
    }

    public void update(String name, Department parentDepartment) {
        this.name = name;
        this.parentDepartment = parentDepartment;
    }
}
