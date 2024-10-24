package com.example.exodia.common.service;

import com.example.exodia.chat.domain.MessageType;
import com.example.exodia.chat.dto.ChatAlarmResponse;
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
    @KafkaListener(topics =
            {
                    "notice-events", "document-events", "submit-events",
                    "family-event-notices", "meeting-room-reservations",
                    "car-reservation-events", "car-reservation-approval-events",
                    "car-reservation-rejection-events"
            }, groupId = "notification-group")
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
            case "meeting-room-reservations":
                processMeetResNotification(message);
                break;
            case "car-reservation-events":
                processCarReservationEvent(message);
                break;
            case "car-reservation-approval-events":
                processCarReservationApproval(message);
                break;
            case "car-reservation-rejection-events":
                processCarReservationRejection(message);
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
    // 회의실 알림 
    public void processMeetResNotification(String message) {
        // 메시지 형식: "userName|departmentId|meetingRoomName|startDate|endDate"
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|");

            // 필드 개수가 정확한지 확인
            if (splitMessage.length == 5) {
                String userName = splitMessage[0];        // 예약자 이름
                String departmentId = splitMessage[1];    // 부서 ID
                String meetingRoomName = splitMessage[2]; // 회의실 이름
                String startDate = splitMessage[3];       // 예약 시작 시간
                String endDate = splitMessage[4];         // 예약 종료 시간

                // 부서 ID로 해당 부서의 모든 사용자 조회
                List<User> departmentUsers = userRepository.findAllByDepartmentId(Long.parseLong(departmentId));

                for (User user : departmentUsers) {
                    String notificationMessage = String.format("%s님이 %s 회의실을 %s ~ %s에 예약하였습니다.", userName, meetingRoomName, startDate, endDate);

                    // 중복 알림 방지
                    boolean exists = notificationRepository.existsByUserAndMessage(user, notificationMessage);
                    if (!exists) {
                        // 알림 저장 및 전송
                        Notification notification = new Notification(user, NotificationType.예약, notificationMessage);
                        notificationRepository.save(notification);

                        // SSE로 실시간 알림 전송
                        NotificationDTO dto = new NotificationDTO(notification);
                        sseEmitters.sendToUser(user.getUserNum(), dto);  // SSE를 통한 실시간 알림
                    } else {
                        System.out.println("이미 동일한 알림이 존재합니다.");
                    }
                }
            } else {
                System.out.println("메시지 형식이 올바르지 않습니다: 필드 수가 잘못되었습니다.");
            }
        } else {
            System.out.println("메시지 형식이 올바르지 않습니다: 구분자가 없습니다.");
        }
    }
    // 차량 예약 요청 이벤트 처리
    private void processCarReservationEvent(String message) {
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|", 4); // 4개 필드로 split
            if (splitMessage.length == 4) {
                String userNum = splitMessage[0]; // userNum으로 변경
                String carNum = splitMessage[1];
                String startDate = splitMessage[2];
                String endDate = splitMessage[3];
                String notificationMessage = String.format("%s님이 %s 차량을 %s부터 %s까지 예약 요청하였습니다.", userNum, carNum, startDate, endDate);

                // 사용자 찾기 시 예외 처리
                User user = userRepository.findByUserNum(userNum)
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userNum));

                // 중복 알림 방지
                boolean exists = notificationRepository.existsByUserAndMessage(user, notificationMessage);
                if (!exists) {
                    // 알림 저장
                    Notification notification = new Notification(user, NotificationType.예약, notificationMessage);
                    notificationRepository.save(notification);

                    // SSE로 실시간 알림 전송
                    NotificationDTO dto = new NotificationDTO(notification);
                    sseEmitters.sendToUser(user.getUserNum(), dto);
                } else {
                    System.out.println("이미 동일한 알림이 존재합니다.");
                }
            } else {
                System.out.println("예약 요청 메시지의 형식이 올바르지 않습니다. 필드가 부족합니다.");
            }
        } else {
            System.out.println("메시지 형식이 올바르지 않습니다: 구분자 '|'가 없습니다.");
        }
    }

    // 차량 예약 승인 이벤트 처리
    private void processCarReservationApproval(String message) {
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|", 3);
            String userNum = splitMessage[0]; // userNum으로 변경
            String carNum = splitMessage[1];
            String submitMessage = splitMessage[2];

            User user = userRepository.findByUserNum(userNum)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 중복 알림 방지
            boolean exists = notificationRepository.existsByUserAndMessage(user, submitMessage);
            if (!exists) {
                // 알림 저장
                Notification notification = new Notification(user, NotificationType.예약, submitMessage);
                notificationRepository.save(notification);

                // SSE로 실시간 알림 전송
                NotificationDTO dto = new NotificationDTO(notification);
                sseEmitters.sendToUser(user.getUserNum(), dto);
            } else {
                System.out.println("이미 동일한 알림이 존재합니다.");
            }
        } else {
            System.out.println("메시지 형식이 올바르지 않습니다.");
        }
    }

    // 차량 예약 거절 이벤트 처리
    private void processCarReservationRejection(String message) {
        if (message.contains("|")) {
            String[] splitMessage = message.split("\\|", 3);
            String userNum = splitMessage[0];
            String carNum = splitMessage[1];
            String submitMessage = splitMessage[2];

            // 사용자 검색
            User user = userRepository.findByUserNum(userNum)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));


            // 중복 알림 여부 확인
            boolean exists = notificationRepository.existsByUserAndMessage(user, submitMessage);
            if (!exists) {
                // 알림 저장
                Notification notification = new Notification(user, NotificationType.예약, submitMessage);
                notificationRepository.save(notification);

                // SSE로 실시간 알림 전송
                NotificationDTO dto = new NotificationDTO(notification);
                sseEmitters.sendToUser(userNum, dto);
                System.out.println("결재 알림 전송 완료: " + submitMessage);
            }
        } else {
            System.out.println("메시지 형식이 올바르지 않습니다.");
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

    // chat-header-alarm-num-update (send(+) + roomEnter(-))
    // chat-list-unread-update (send)
    @Transactional
    @KafkaListener(topics = {"sendChatAlarm-events", "enterChatAlarm-events", "chatRoomList-events"}, groupId = "chat-group")
    public void listenChatEvents(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, String message) {
        System.out.println("Kafka 메시지 수신: " + message);

        switch (topic) {
            case "sendChatAlarm-events":
                processSendChatHeaderAlarmUpdateMessage(message);
                break;
            case "enterChatAlarm-events":
                processEnterChatHeaderAlarmUpdateMessage(message);
                break;
            case "chatRoomList-events":
                processChatRoomListUnreadUpdateMessage(message);
                break;
            default:
                System.out.println("알 수 없는 토픽이거나 메시지 형식이 맞지 않습니다.");
        }

    }

    // chat-header-alarm-num-update (send(+))
    private void processSendChatHeaderAlarmUpdateMessage(String message) {
        // 메시지 형식: "0userNum(receiverNum)|1senderName|2roomName|3messageType|4message|5alarmNum"

        if(message.contains("|")){
            String[] messages = message.split("\\|");
            System.out.println(messages);

            if(messages[3].equals("FILE")){
                sseEmitters.sendChatToUser(messages[0], ChatAlarmResponse.builder()
                        .type("채팅알림")
                        .senderName(messages[1])
                        .roomName(messages[2])
                        .message("FILE 전송")
                        .alarmNum(Integer.parseInt(messages[5]))
                        .build());
            }else{
                sseEmitters.sendChatToUser(messages[0], ChatAlarmResponse.builder()
                        .type("채팅알림")
                        .senderName(messages[1])
                        .roomName(messages[2])
                        .message(messages[4])
                        .alarmNum(Integer.parseInt(messages[5]))
                        .build());
            }
        }
    }

    // chat-header-alarm-num-update (roomEnter(-))
    private void processEnterChatHeaderAlarmUpdateMessage(String message) {
        // 메시지 형식: "0userNum(receiverNum)|1|2|3|4|alarmNum"
        if(message.contains("|")){
            String[] messages = message.split("\\|");
            sseEmitters.sendChatToUser(messages[0], ChatAlarmResponse.builder()
                    .type("채팅입장")
                    .senderName(messages[1])
                    .roomName(messages[2])
                    .message(messages[4])
                    .alarmNum(Integer.parseInt(messages[5]))
                    .build());
        }
    }

    // chat-list-unread-update (send)
    private void processChatRoomListUnreadUpdateMessage(String message) {
        // 메시지 형식: "0userNum(receiverNum)|1|2|3|4|5"
        if(message.contains("|")){
            String[] messages = message.split("\\|");
            sseEmitters.sendChatToUser(messages[0], ChatAlarmResponse.builder()
                    .type("채팅목록")
                    .senderName(messages[1])
                    .roomName(messages[2])
                    .message(messages[4])
                    .alarmNum(Integer.parseInt(messages[5]))
                    .build());
        }
    }
}



