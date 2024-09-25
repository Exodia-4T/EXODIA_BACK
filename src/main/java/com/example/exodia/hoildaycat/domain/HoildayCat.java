package com.example.exodia.hoildaycat.domain;

import com.example.exodia.holiday.domain.Hoilday;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "del_yn = 'N'")
public class HoildayCat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String holidayName;

    @Column(nullable = false)
    private String hoildayGive;

    @OneToMany(mappedBy = "hoildayCat")
    private List<Hoilday> hoildays;
}
