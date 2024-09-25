package com.example.exodia.common.data;

import com.example.exodia.common.domain.DelYN;
import com.example.exodia.department.domain.Department;
import com.example.exodia.evalutionb.domain.Evalutionb;
import com.example.exodia.evalutionb.repository.EvalutionbRepository;
import com.example.exodia.evalutionm.domain.Evalutionm;
import com.example.exodia.evalutionm.repository.EvalutionmRepository;
import com.example.exodia.position.domain.Position;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.domain.Gender;
import com.example.exodia.user.domain.Status;
import com.example.exodia.user.domain.HireType;
import com.example.exodia.user.domain.NowStatus;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.department.repository.DepartmentRepository;
import com.example.exodia.position.repository.PositionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final EvalutionbRepository evalutionbRepository;
    private final EvalutionmRepository evalutionmRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataLoader(DepartmentRepository departmentRepository,
                             PositionRepository positionRepository,
                             UserRepository userRepository, EvalutionbRepository evalutionbRepository, EvalutionmRepository evalutionmRepository,
                             PasswordEncoder passwordEncoder) {
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
        this.evalutionbRepository = evalutionbRepository;
        this.evalutionmRepository = evalutionmRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Department hrDepartment = new Department(null, "인사팀");
        Department webDevDepartment = new Department(null, "웹개발팀");
        departmentRepository.save(hrDepartment);
        departmentRepository.save(webDevDepartment);

        Position teamLeader = new Position(null, "팀장");
        Position director = new Position(null, "부장");
        positionRepository.save(teamLeader);
        positionRepository.save(director);

        String Password1 = passwordEncoder.encode("testtest");
        String Password2 = passwordEncoder.encode("testtest");

        User user1 = new User(
                null,
                "20240901001",
                null,
                "test1",
                Gender.M,
                Status.재직,
                Password1,
                "test1@test",
                "어양동",
                "01012345678",
                DelYN.N,
                "123456-1234567",
                HireType.정규직,
                NowStatus.출근,
                15,
                hrDepartment,
                teamLeader,
                0
        );

        User user2 = new User(
                null,
                "20240901002",
                null,
                "test2",
                Gender.W,
                Status.재직,
                Password2,
                "test2@test",
                "영등동",
                "01098765432",
                DelYN.N,
                "123456-2345678",
                HireType.계약직,
                NowStatus.회의,
                15,
                webDevDepartment,
                director,
                0
        );

        userRepository.save(user1);
        System.out.println("User1 saved: " + user1.getUserNum());
        userRepository.save(user2);
        System.out.println("User2 saved: " + user2.getUserNum());
        // 대분류 데이터 생성
        Evalutionb workAbility = new Evalutionb(null, "업무 수행 능력");
        Evalutionb problemSolving = new Evalutionb(null, "문제 해결");
        Evalutionb responsibility = new Evalutionb(null, "책임감");
        Evalutionb teamworkCommunication = new Evalutionb(null, "팀워크/의사소통");

        evalutionbRepository.save(workAbility);
        evalutionbRepository.save(problemSolving);
        evalutionbRepository.save(responsibility);
        evalutionbRepository.save(teamworkCommunication);

        // 중분류 데이터 생성 및 대분류와 연결
        Evalutionm workAchievement = new Evalutionm(null, "업무달성도", workAbility);
        Evalutionm workProcessingAbility = new Evalutionm(null, "업무처리능력", workAbility);

        Evalutionm problemSolvingAbility = new Evalutionm(null, "문제 해결 능력", problemSolving);
        Evalutionm initiative = new Evalutionm(null, "주도성", problemSolving);

        Evalutionm cooperativeAttitude = new Evalutionm(null, "협력태도", responsibility);

        Evalutionm teamwork = new Evalutionm(null, "팀워크", teamworkCommunication);
        Evalutionm communication = new Evalutionm(null, "의사소통", teamworkCommunication);

        evalutionmRepository.save(workAchievement);
        evalutionmRepository.save(workProcessingAbility);
        evalutionmRepository.save(problemSolvingAbility);
        evalutionmRepository.save(initiative);
        evalutionmRepository.save(cooperativeAttitude);
        evalutionmRepository.save(teamwork);
        evalutionmRepository.save(communication);

        System.out.println("Initial Evalution data loaded.");
    }
}