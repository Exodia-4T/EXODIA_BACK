package com.example.exodia.user.domain;

import com.example.exodia.common.domain.BaseTimeEntity;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.user.dto.UserRegisterDto;
import com.example.exodia.department.domain.Department;
import com.example.exodia.position.domain.Position;
import com.example.exodia.user.dto.UserUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
//@Where(clause = "del_yn = 'N'")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_num", nullable = false, length = 12, unique = true)
    private String userNum;

    private String profileImage;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String address;

    @Column(nullable = false, length = 11)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn", nullable = false)
    private DelYN delYn = DelYN.N;

    @Column(nullable = false, length = 20)
    private String socialNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HireType hireType;

    @Column(length = 100)
    private NowStatus n_status;

    @Column(nullable = false)
    private int annualLeave;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(nullable = false)
    private int loginFailCount = 0;


    public void incrementLoginFailCount() {
        this.loginFailCount += 1;
    }

    public void resetLoginFailCount() {
        this.loginFailCount = 0;
    }

    public HireType getHireType() {
        return this.hireType;
    }

    public int getAnnualLeave() {
        return this.annualLeave;
    }

    public static User fromRegisterDto(UserRegisterDto dto, Department department, Position position, String encodedPassword) {
        return new User(
                null,
                dto.getUserNum(),
                dto.getProfileImage(),
                dto.getName(),
                Gender.valueOf(dto.getGender()),
                Status.valueOf(dto.getStatus()),
                encodedPassword,
                dto.getEmail(),
                dto.getAddress(),
                dto.getPhone(),
                DelYN.N,
                dto.getSocialNum(),
                dto.getHireType(),
                dto.getNowStatus(),
                dto.getAnnualLeave(),
                department,
                position,
                0
        );
    }

    public void updateFromDto(UserUpdateDto dto, Department department, Position position) {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        this.address = dto.getAddress();
        this.hireType = dto.getHireType();
        this.annualLeave = dto.getAnnualLeave();
        this.department = department;
        this.position = position;
    }

    @Override
    public void softDelete() {
        super.softDelete();
    }


//    test코드
    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public User(String userNum) {
        this.userNum = userNum;
    }

}

