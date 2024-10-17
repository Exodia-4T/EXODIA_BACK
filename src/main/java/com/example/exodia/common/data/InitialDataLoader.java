package com.example.exodia.common.data;


import com.example.exodia.car.domain.Car;
import com.example.exodia.car.repository.CarRepository;
import com.example.exodia.chat.domain.ChatRoom;
import com.example.exodia.chat.domain.ChatUser;
import com.example.exodia.chat.repository.ChatRoomRepository;
import com.example.exodia.chat.repository.ChatUserRepository;

import com.example.exodia.department.domain.Department;
import com.example.exodia.document.domain.Tag;
import com.example.exodia.document.repository.TagRepository;
import com.example.exodia.evalutionFrame.evalutionBig.domain.Evalutionb;
import com.example.exodia.evalutionFrame.evalutionBig.repository.EvalutionbRepository;
import com.example.exodia.evalutionFrame.evalutionMiddle.domain.Evalutionm;
import com.example.exodia.evalutionFrame.evalutionMiddle.repository.EvalutionmRepository;
import com.example.exodia.meetingRoom.domain.MeetingRoom;
import com.example.exodia.meetingRoom.repository.MeetingRoomRepository;
import com.example.exodia.position.domain.Position;
import com.example.exodia.salary.domain.Salary;
import com.example.exodia.salary.repository.SalaryRepository;
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
import java.util.List;

@Component
public class InitialDataLoader implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final EvalutionbRepository evalutionbRepository;
    private final EvalutionmRepository evalutionmRepository;
    private final PasswordEncoder passwordEncoder;
    private final MeetingRoomRepository meetingRoomRepository;
    private final CarRepository carRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    private final SalaryRepository salaryRepository;
    private final TagRepository tagRepository;

    private final double NATIONAL_PENSION_RATE = 0.045;
    private final double HEALTH_INSURANCE_RATE = 0.03545;
    private final double LONG_TERM_CARE_INSURANCE_RATE = 0.004591;
    private final double EMPLOYMENT_INSURANCE_RATE = 0.009;


    public InitialDataLoader(DepartmentRepository departmentRepository,
                             PositionRepository positionRepository,
                             UserRepository userRepository,
                             EvalutionbRepository evalutionbRepository,
                             EvalutionmRepository evalutionmRepository,
                             PasswordEncoder passwordEncoder,
                             MeetingRoomRepository meetingRoomRepository,
                             ChatRoomRepository chatRoomRepository,
                             ChatUserRepository chatUserRepository,
                             CarRepository carRepository,
                             SalaryRepository salaryRepository, TagRepository tagRepository) {  // Constructor에 추가
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
        this.evalutionbRepository = evalutionbRepository;
        this.evalutionmRepository = evalutionmRepository;
        this.passwordEncoder = passwordEncoder;
        this.meetingRoomRepository = meetingRoomRepository;
        this.carRepository = carRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatUserRepository = chatUserRepository;
        this.salaryRepository = salaryRepository;  // 주입
		this.tagRepository = tagRepository;
	}

    @Override
    public void run(String... args) throws Exception {
        // 본부 생성
        Department salesHQ = new Department("영업본부", null);
        Department devHQ = new Department("개발본부", null);
        Department supportHQ = new Department("지원본부", null);
        departmentRepository.save(salesHQ);
        departmentRepository.save(devHQ);
        departmentRepository.save(supportHQ);

        // 지원본부 밑에 인사팀 추가
        Department hrDepartment = new Department("인사팀", supportHQ);
        departmentRepository.save(hrDepartment);

        // 경영지원부서 밑에 경영1팀 추가
        Department managementDepartment = new Department("경영지원부서", supportHQ);
        departmentRepository.save(managementDepartment);

        Department managementTeam1 = new Department("경영1팀", managementDepartment);
        departmentRepository.save(managementTeam1);

        // 직위 추가 - 각 직급에 기본 연봉(baseSalary) 추가
        Position teamLeader = new Position(null, "팀장", 80000000.0);
        Position director = new Position(null, "부장", 70000000.0);
        Position assistantManager = new Position(null, "대리", 48000000.0);
        Position test = new Position(null, "주임", 42000000.0);
        Position test1 = new Position(null, "과장", 58000000.0);
        Position basicPerson = new Position(null, "사원", 36000000.0); //
        positionRepository.save(teamLeader);
        positionRepository.save(director);
        positionRepository.save(assistantManager);
        positionRepository.save(basicPerson);
        positionRepository.save(test);
        positionRepository.save(test1);


        // 사용자 추가
        String password1 = passwordEncoder.encode("testtest");
        String password2 = passwordEncoder.encode("testtest");
        String password3 = passwordEncoder.encode("testtest");

        User user1 = new User(
                null,
                "20240901001",
                null,
                "이예나",
                Gender.M,
                Status.재직,
                password1,
                "test1@test",
                "어양동",
                "01012345678",
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
                "https://exodia-file.s3.ap-northeast-2.amazonaws.com/document/user.png",
                "김수연",
                Gender.W,
                Status.재직,
                password2,
                "test2@test",
                "영등동",
                "01098765432",
                "123456-2345678",
                HireType.계약직,
                NowStatus.회의,
                15,
                hrDepartment,
                director,
                0
        );

        User user3 = new User(
                null,
                "20240901003",
                "https://exodia-file.s3.ap-northeast-2.amazonaws.com/document/user.png",
                "test3",
                Gender.W,
                Status.재직,
                password3,
                "test3@test",
                "신촌동",
                "01033333333",
                "123456-3456789",
                HireType.정규직,
                NowStatus.회의,
                10,
                managementTeam1,
                assistantManager,
                0
        );

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Salary 데이터 생성을 위한 더미 사용자
        Salary salary1 = Salary.builder()
                .user(user1)
                .baseSalary(user1.getPosition().getBaseSalary())  // 직급에 맞는 기본 연봉 설정
                .taxAmount(
                        Salary.TaxAmount.builder()
                                .nationalPension(user1.getPosition().getBaseSalary() * NATIONAL_PENSION_RATE)
                                .healthInsurance(user1.getPosition().getBaseSalary() * HEALTH_INSURANCE_RATE)
                                .longTermCare(user1.getPosition().getBaseSalary() * LONG_TERM_CARE_INSURANCE_RATE)
                                .employmentInsurance(user1.getPosition().getBaseSalary() * EMPLOYMENT_INSURANCE_RATE)
                                .totalTax(500000.0)
                                .build()
                )
                .finalSalary(user1.getPosition().getBaseSalary() - 500000.0)  // 세금 제외 후 최종 연봉
                .build();

        Salary salary2 = Salary.builder()
                .user(user2)
                .baseSalary(user2.getPosition().getBaseSalary())  // 직급에 맞는 기본 연봉 설정
                .taxAmount(
                        Salary.TaxAmount.builder()
                                .nationalPension(user2.getPosition().getBaseSalary() * NATIONAL_PENSION_RATE)
                                .healthInsurance(user2.getPosition().getBaseSalary() * HEALTH_INSURANCE_RATE)
                                .longTermCare(user2.getPosition().getBaseSalary() * LONG_TERM_CARE_INSURANCE_RATE)
                                .employmentInsurance(user2.getPosition().getBaseSalary() * EMPLOYMENT_INSURANCE_RATE)
                                .totalTax(700000.0)
                                .build()
                )
                .finalSalary(user2.getPosition().getBaseSalary() - 700000.0)  // 세금 제외 후 최종 연봉
                .build();

        // 더미 데이터를 저장
        salaryRepository.save(salary1);
        salaryRepository.save(salary2);


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

        /* 회의실 기본 제공 데이터 */
        List<MeetingRoom> initialMeetingRooms = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            MeetingRoom meetingRoom = MeetingRoom.builder()
                    .name("회의실 " + i)
                    .build();
            initialMeetingRooms.add(meetingRoom);
        }
        meetingRoomRepository.saveAll(initialMeetingRooms);


        List<Car> cars = new ArrayList<>();
        cars.add(new Car(null, "121가123", "스타랙스", 11, 2.5, "starrex.jpg"));
        cars.add(new Car(null, "135나894", "밴츠", 5, 3.0, "benz.jpg"));
        cars.add(new Car(null, "753호159", "SUV", 7, 2.0, "suv.jpg"));
        cars.add(new Car(null, "143라3451", "람보르기니", 2, 5.2, "lamborghini.jpg"));
        cars.add(new Car(null, "429호7318", "G70", 5, 3.3, "g70.jpg"));
        cars.add(new Car(null, "14라 8222", "소나타", 5, 2.0, "sonata.jpg"));
        cars.add(new Car(null, "18유3752", "황금마티즈", 4, 0.8, "matiz.jpg"));
        carRepository.saveAll(cars);

        ChatRoom chatRoom = ChatRoom.builder().roomName("test").build();
        chatRoomRepository.save(chatRoom);
        ChatUser chatUser1 = ChatUser.builder().chatRoom(chatRoom).user(user1).build();
        ChatUser chatUser2 = ChatUser.builder().chatRoom(chatRoom).user(user2).build();
        chatUserRepository.save(chatUser1);
        chatUserRepository.save(chatUser2);

        tagRepository.save(new Tag(1L, "회의록"));
        tagRepository.save(new Tag(2L, "프로젝트 계획서"));
        tagRepository.save(new Tag(3L, "성과 보고서"));
        tagRepository.save(new Tag(4L, "EXODIA-POT"));
    }
}
