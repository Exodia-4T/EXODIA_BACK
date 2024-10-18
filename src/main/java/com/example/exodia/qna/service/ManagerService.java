package com.example.exodia.qna.service;

import com.example.exodia.qna.domain.Manager;
import com.example.exodia.qna.dto.ManagerListDto;
import com.example.exodia.qna.dto.ManagerSaveDto;
import com.example.exodia.qna.repository.ManagerRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;

    public ManagerListDto saveManager(ManagerSaveDto managerSaveDto) {
        User user = userRepository.findById(managerSaveDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Manager manager = new Manager();
        manager.setUser(user);

        // 엔티티를 저장하고, DTO로 변환하여 반환
        managerRepository.save(manager);

        return new ManagerListDto(manager.getId(), user.getUserNum(), user.getDepartment().getId(), user.getPosition().getId(), user.getName());
    }



    // 2. 매니저 삭제
    public void deleteManager(Long managerId) {
        // 매니저 존재 여부 확인 후 삭제
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("매니저를 찾을 수 없습니다."));
        managerRepository.delete(manager);
    }

    // 3. 매니저 목록 조회
    public List<ManagerListDto> getAllManagers() {
        List<Manager> managers = managerRepository.findAll();

        // 매니저 리스트를 ManagerListDto로 변환하여 반환
        return managers.stream()
                .map(ManagerListDto::fromEntity)
                .collect(Collectors.toList());
    }
}
