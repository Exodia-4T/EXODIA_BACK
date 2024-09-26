package com.example.exodia.user.service;

import com.example.exodia.common.auth.JwtTokenProvider;
import com.example.exodia.common.domain.DelYN;
import com.example.exodia.department.domain.Department;
import com.example.exodia.department.repository.DepartmentRepository;
import com.example.exodia.position.domain.Position;
import com.example.exodia.position.repository.PositionRepository;
import com.example.exodia.user.domain.Status;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.dto.*;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.userDelete.domain.DeleteHistory;
import com.example.exodia.userDelete.repository.DeleteHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DeleteHistoryRepository deleteHistoryRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DeleteHistoryRepository deleteHistoryRepository, DepartmentRepository departmentRepository, PositionRepository positionRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.deleteHistoryRepository = deleteHistoryRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(UserLoginDto loginDto) {
        User user = userRepository.findByUserNumAndDelYn(loginDto.getUserNum(), DelYN.N)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));
        if (user.isDeleted()) {
            throw new RuntimeException("비활성화 상태의 계정입니다.");
        }
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            user.incrementLoginFailCount();
            if (user.getLoginFailCount() >= 5) {
                user.softDelete();
            }
            userRepository.save(user);
            throw new RuntimeException("잘못된 이메일/비밀번호 입니다.");
        }
        user.resetLoginFailCount();
        userRepository.save(user);
        return jwtTokenProvider.createToken(user.getUserNum(),
                user.getDepartment().getId(),
                user.getPosition().getId());
    }

    public User registerUser(UserRegisterDto registerDto, String departmentName) {
        checkHrAuthority(departmentName);
        Department department = departmentRepository.findById(registerDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다."));
        Position position = positionRepository.findById(registerDto.getPositionId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 직급입니다."));
        User user = User.fromRegisterDto(registerDto, department, position, registerDto.getPassword());
        return userRepository.save(user);
    }

    public User updateUser(String userNum, UserUpdateDto updateDto, String departmentId) {
        checkHrAuthority(departmentId);
        User user = userRepository.findByUserNumAndDelYn(userNum, DelYN.N)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        Department department = departmentRepository.findById(updateDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 부서입니다."));

        Position position = positionRepository.findById(updateDto.getPositionId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 직급입니다."));

        user.updateFromDto(updateDto, department, position);
        return userRepository.save(user);
    }

    public void checkHrAuthority(String departmentName) {
        if (!"1".equals(departmentName)) {
            throw new RuntimeException("권한이 없습니다. 관리자만 수행할 수 있습니다.");
        }
    }

    public List<UserInfoDto> getAllUsers() {
        List<User> users = userRepository.findAllByDelYn(DelYN.N);
        return users.stream()
                .map(UserInfoDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDetailDto getUserDetail(String userNum) {
        User user = userRepository.findByUserNumAndDelYn(userNum, DelYN.N)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
        return UserDetailDto.fromEntity(user);
    }

    @Transactional
    public void deleteUser(UserDeleteDto deleteDto, String departmentId) {
        checkHrAuthority(departmentId);
        User user = userRepository.findByUserNumAndDelYn(deleteDto.getUserNum(), DelYN.N)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));
        user.softDelete();
        userRepository.save(user);

        DeleteHistory deleteHistory = new DeleteHistory(
                deleteDto.getDeletedBy(),
                deleteDto.getReason(),
                user
        );
        deleteHistoryRepository.save(deleteHistory);
    }
}
