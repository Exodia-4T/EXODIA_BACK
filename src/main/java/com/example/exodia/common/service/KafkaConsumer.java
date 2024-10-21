package com.example.exodia.common.service;

import com.example.exodia.notification.domain.Notification;
import com.example.exodia.notification.domain.NotificationType;
import com.example.exodia.notification.dto.NotificationDTO;
import com.example.exodia.notification.repository.NotificationRepository;
import com.example.exodia.notification.service.NotificationService;
import com.example.exodia.registration.domain.Registration;
import com.example.exodia.registration.service.RegistrationService;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KafkaConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final SseEmitters sseEmitters;
    private final RegistrationService registrationService;

    @Autowired
    public KafkaConsumer(NotificationRepository notificationRepository, NotificationService notificationService,
                         UserRepository userRepository, SseEmitters sseEmitters, RegistrationService registrationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.sseEmitters = sseEmitters;
        this.registrationService = registrationService;
    }

    @Transactional
    @KafkaListener(topics = {"notice-events", "document-events", "submit-events", "family-event-notices"}, groupId = "notification-group")
    public void listen(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        System.out.println("Kafka 메시지 수신: " + message);

        switch (topic) {
            case "document-events":
                processDocumentUpdateMessage(message);
                break;
            case "notice-events":
                processBoardNotification(message);
                break;
            case "family-event-notices":
                processFamilyEventNotification(message);
                break;
            case "submit-events":
                processSubmitNotification(message);
                break;
            default:
                System.out.println("알 수 없는 토픽이거나 메시지 형식이 맞지 않습니다.");
        }
    }

    @Transactional
    @KafkaListener(topics = {"document-events"}, groupId = "notification-group")
    public void listenDocumentUpdateEvents(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        System.out.println("Kafka 메시지 수신: " + message);

        if ("document-events".equals(topic)) {
            processDocumentUpdateMessage(message);
        }
        System.out.println("feat/test");
    }

    private void processDocumentUpdateMessage(String message) {
        // 메시지 형식: "부서ID|문서 업데이트 메시지"
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|", 2);
            String departmentId = splitMessage[0];  // 부서 ID
            String actualMessage = splitMessage[1]; // 알림 메시지

            // 해당 부서의 모든 사용자에게 알림 전송
            List<User> departmentUsers = userRepository.findAllByDepartmentId(Long.parseLong(departmentId));
            for (User user : departmentUsers) {
                boolean exists = notificationRepository.existsByUserAndMessage(user, actualMessage);
                if (!exists) {
                    Notification notification = new Notification(user, NotificationType.문서, actualMessage);
                    notificationRepository.save(notification);

                    NotificationDTO dto = new NotificationDTO(notification);
                    sseEmitters.sendToUser(user.getUserNum(), dto); // SSE로 실시간 알림 전송
                }
            }
        }
    }
    // 결재 알림 처리
    private void processSubmitNotification(String message) {
        // 메시지 형식: "userNum|submitMessage"
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|", 2);
            String userNum = splitMessage[0];
            String submitMessage = splitMessage[1];

            User user = userRepository.findByUserNum(userNum)
                    .orElseThrow(() -> new EntityNotFoundException("회원정보가 존재하지 않습니다."));

            boolean exists = notificationRepository.existsByUserAndMessage(user, submitMessage);
            if (!exists) {
                Notification notification = new Notification(user, NotificationType.결재, submitMessage);
                notificationRepository.save(notification);

                // SSE로 실시간 알림 전송
                NotificationDTO dto = new NotificationDTO(notification);
                sseEmitters.sendToUser(userNum, dto);
                System.out.println("결재 알림 전송 완료: " + submitMessage);
            }
        }
    }

    // 경조사 알림 처리 로직
    private void processFamilyEventNotification(String message) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            boolean exists = notificationRepository.existsByUserAndMessage(user, message);
            if (!exists) {
                Notification notification = new Notification(user, NotificationType.경조사, message);
                notificationRepository.save(notification);

                NotificationDTO dto = new NotificationDTO(notification);
                sseEmitters.sendToUser(user.getUserNum(), dto); // SSE로 전송
            }
        }
    }

    // 공지사항 알림 처리 로직
    private void processBoardNotification(String message) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            boolean exists = notificationRepository.existsByUserAndMessage(user, message);
            if (!exists) {
                Notification notification = new Notification(user, NotificationType.공지사항, message);
                notificationRepository.save(notification);

                NotificationDTO dto = new NotificationDTO(notification);
                sseEmitters.sendToUser(user.getUserNum(), dto);
            }
        }
    }




    @KafkaListener(topics = "course-registration", groupId = "course-registration-group")
    public void listenCourseRegistration(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        System.out.println("Kafka 참가자 등록 메시지 수신: " + message);

        String[] messageParts = message.split(" has registered for course ");
        String userNum = messageParts[0].split(" ")[1];
        Long courseId = Long.parseLong(messageParts[1]); // course 1 에서 1 추출

        registrationService.confirmRegistration(courseId, userNum);
    }

    @KafkaListener(topics = {"course-transmission"}, groupId = "course-transmission-group")
    public void listenCourseTransmission(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        System.out.println("Kafka 강좌 전송 메시지 수신: " + message);

        // 메시지 형식: "courseId|전송 메시지"
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|", 2);
            String courseId = splitMessage[0];
            String transmissionMessage = splitMessage[1];

            // 강좌 전송 이벤트에 대해 처리할 로직
            List<User> users = userRepository.findAll();
            for (User user : users) {
                boolean exists = notificationRepository.existsByUserAndMessage(user, transmissionMessage);
                if (!exists) {
                    Notification notification = new Notification(user, NotificationType.강좌, transmissionMessage);
                    notificationRepository.save(notification);

                    NotificationDTO dto = new NotificationDTO(notification);
                    sseEmitters.sendToUser(user.getUserNum(), dto);
                }
            }

            System.out.println("강좌 전송 알림 처리 완료: " + transmissionMessage);
        }
    }
}



