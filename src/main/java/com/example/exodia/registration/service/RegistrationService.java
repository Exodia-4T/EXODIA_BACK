package com.example.exodia.registration.service;


import com.example.exodia.common.service.KafkaProducer;
import com.example.exodia.course.domain.Course;
import com.example.exodia.course.repository.CourseRepository;
import com.example.exodia.registration.domain.Registration;
import com.example.exodia.registration.dto.RegistrationCreateDto;
import com.example.exodia.registration.dto.RegistrationDto;
import com.example.exodia.registration.repository.RegistrationRepository;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RegistrationService {
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaProducer kafkaProducer;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(RedissonClient redissonClient, @Qualifier("12") RedisTemplate<String, Object> redisTemplate,
                               KafkaProducer kafkaProducer, CourseRepository courseRepository, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
        this.kafkaProducer = kafkaProducer;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional
    public String registerParticipant(Long courseId) {
        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("강좌를 찾을 수 없습니다."));

        if (registrationRepository.existsByCourseAndUser(course, user)) {
            return "이미 해당 강좌에 등록된 사용자입니다.";
        }

        String redisKey = "course:" + courseId + ":participants";
        RLock lock = redissonClient.getLock("courseLock:" + courseId);  // Redisson 락 생성

        try {
            // 락을 걸고 10초 안에 작업 완료, 락이 풀리지 않으면 30초 후 자동 해제
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    Long currentParticipants = redisTemplate.opsForValue().increment(redisKey); // 참가자 수 증가

                    if (currentParticipants > getMaxParticipants(courseId)) {
                        // 초과 시 참가자 수 롤백
                        redisTemplate.opsForValue().decrement(redisKey);
                        return "참가자 수가 초과되었습니다.";
                    }

                    // Kafka producer를 통해 등록 이벤트 전송
                    String message = "User " + userNum + " has registered for course " + courseId;
                    kafkaProducer.sendCourseRegistrationEvent(courseId.toString(), message);

                    return "등록 완료";
                } finally {
                    lock.unlock();  // 작업이 끝나면 락 해제
                }
            } else {
                return "잠시 후 다시 시도해 주세요.";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "등록 처리 중 문제가 발생했습니다.";
        }
    }

    // 데이터베이스에서 강좌의 최대 참가자 수 조회
    public int getMaxParticipants(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("강좌를 찾을 수 없습니다."));
        return course.getMaxParticipants();
    }


    // 강좌 등록
    @Transactional
    public void confirmRegistration(Long courseId, String userNum) {

        User user = userRepository.findByUserNum(userNum)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("강좌를 찾을 수 없습니다."));

        // 이미 등록된 사용자인지 확인
        if (registrationRepository.existsByCourseAndUser(course, user)) {
            System.out.println("이미 등록된 사용자입니다.");
            return;
        }

        Registration participant = new RegistrationCreateDto(courseId, userNum, "confirmed").toEntity(course, user);

        registrationRepository.save(participant);
        System.out.println("User " + userNum + "가 강좌 " + courseId + "에 성공적으로 등록되었습니다.");//
    }

    @Transactional
    public List<RegistrationDto> getConfirmedParticipants(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("강좌를 찾을 수 없습니다."));

        return registrationRepository.findAllByCourseAndRegistrationStatus(course, "confirmed").stream()
                .map(registration -> new RegistrationDto(registration.getUser().getUserNum(), registration.getUser().getName()))
                .collect(Collectors.toList());
    }



    // 현재 강좌의 참가자 수를 Redis에서 조회하는 메서드
    public int getCurrentParticipantCount(Long courseId) {
        String redisKey = "course:" + courseId + ":participants";
        Integer currentParticipants = (Integer) redisTemplate.opsForValue().get(redisKey);

        if (currentParticipants == null) {
            return 0;
        }

        return currentParticipants;
    }
}