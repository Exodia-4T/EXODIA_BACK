package com.example.exodia.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //    public void sendBoardEvent(String topic, String message) {
//        String uniqueKey = UUID.randomUUID().toString();
//        kafkaTemplate.send(topic, message);
//        System.out.println("Kafka 이벤트 : " + message); //
//        System.out.println(topic);
//    }
    // 알림 제어
    public void sendNotificationEvent(String userNum, String content) {
        String message = userNum + "|" + content; // 메시지 포맷: "userNum|content"
        kafkaTemplate.send("notification-topic", message);
        System.out.println("Kafka 알림 이벤트 전송: " + message);
    }

    // 게시판 알림
    public void sendBoardEvent(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Kafka 이벤트 전송: " + message + " / 토픽: " + topic);
    }
    // 회의실 예약 알림
//    public void sendMeetingReservationNotification(String topic, String userName, String meetingRoomName, String startDate, String endDate, String departmentId) {
//        // "|" 구분자를 사용하여 필드 구분
//        String message = String.format("%s|%s|%s|%s|%s", userName, departmentId, meetingRoomName, startDate, endDate);
//        kafkaTemplate.send(topic, message);
//        System.out.println("Kafka 회의실 예약 이벤트 전송: " + message + " / 토픽: " + topic);
//    }
    // Q&A 이벤트 전송 메서드
    public void sendQnaEvent(String eventType, String departmentId, String userNum, String message) {
        String payload = String.format("%s|%s|%s|%s", eventType, departmentId, userNum, message);
        kafkaTemplate.send("qanda-events", payload);
        System.out.println("Kafka Q&A 이벤트 전송: " + message + " / 토픽: qanda-events");
    }

    // 차량 예약 요청 알림 전송
    public void sendCarReservationNotification(String topic, String userNum, String userName, String carNum, String startDate, String endDate) {
        String message = String.format("%s|%s|%s|%s|%s", userNum, userName, carNum, startDate, endDate);
        kafkaTemplate.send(topic, message);
        System.out.println("Kafka 차량 예약 요청 전송: " + message + " / 토픽: " + topic);
    }

    // 차량 예약 승인 알림 전송
    public void sendReservationApprovalNotification(String topic, String userNum, String carNum, String startTime, String endTime) {
        String message = String.format("%s|%s|%s|%s|인사팀에서 %s ~ %s 의 차량 예약을 승인하였습니다.",
                userNum, carNum, startTime, endTime, startTime, endTime);
        kafkaTemplate.send(topic, message);
        System.out.println("Kafka 차량 예약 승인 전송: " + message + " / 토픽: " + topic);
    }

    // 차량 예약 거절 알림 전송
    public void sendReservationRejectionNotification(String topic, String userNum, String carNum, String startTime, String endTime) {
        String message = String.format("%s|%s|%s|%s|인사팀에서 %s ~ %s 의 차량 예약을 거절하였습니다.",
                userNum, carNum, startTime, endTime, startTime, endTime);
        kafkaTemplate.send(topic, message);
        System.out.println("Kafka 차량 예약 거절 전송: " + message + " / 토픽: " + topic);
    }

    // 결재 알림 전송
    public void sendSubmitNotification(String topic, String userName, String userNum, String date) {
        String message = String.format("%s 님에게 %s일에 결재 요청이 도착했습니다", userName, date);
        kafkaTemplate.send(topic, userNum + "|" + message);
        System.out.println("Kafka 결재 알림 이벤트: " + message);
    }

    // 결재 반려
    public void revokeSubmitNotification(String topic, String userNum) {
        String message = String.format("결재가 반려되었습니다 ");
        kafkaTemplate.send(topic, userNum + "|" + message);
        System.out.println("kafka 결재 반려 알림 이벤트" + message);
    }

    //결재 최종 승인
    public void allSubmitNotification(String topic, String userNum) {
        String message = String.format("결재가 최종 승인되었습니다");
        kafkaTemplate.send(topic, userNum + "|" + message);
        System.out.println("kafka 결재 최종 승인 이벤트" + message);
    }

    // 문서 업데이트 알림을 위한 메서드 (부서 ID 포함)
    public void sendDocumentUpdateEvent(String topic, String fileName, String userName, String departmentId) {
        String message = String.format("%s 님이 %s 을 업데이트 했습니다", userName, fileName);
        kafkaTemplate.send(topic, departmentId + "|" + message);
        System.out.println("Kafka 문서 업데이트 이벤트: " + message);
    }

    // 문서 롤백 알림을 위한 메서드 (부서 ID 포함)
    public void sendDocumentRollBackEvent(String topic, String fileName, String userName, String departmentId) {
        String message = String.format("%s 님이 %s 을 이전버전으로 복원했습니다", userName, fileName);
        kafkaTemplate.send(topic, departmentId + "|" + message);
        System.out.println("Kafka 문서 롤백 이벤트: " + message);
    }

    // 강좌
    public void sendCourseRegistrationEvent(String courseId, String message) {
        kafkaTemplate.send("course-registration", courseId, message);
        System.out.println("Kafka 강좌 등록 이벤트 전송: " + message + " / 코스 ID: " + courseId);
    }

    public void sendCourseTransmissionEvent(String courseId, String message) {
        kafkaTemplate.send("course-transmission", courseId, message);
        System.out.println("Kafka 강좌 전송 이벤트 전송: " + message + " / 코스 ID: " + courseId);
    }

    // chat-header-alarm-num-update (send(+))
    // ⭐⭐ 따로 아래로 알림표시가 안되고 숫자만 올라간다. 뭔가 따로 알림이 갔으면 좋겠는데...
    public void sendChatAlarmEvent(String userNum, String message){
        kafkaTemplate.send("sendChatAlarm-events", userNum+ "|" +message);
        System.out.println("Kafka 채팅 알림 이벤트 전송: " + message);
    }

    // chat-header-alarm-num-update (roomEnter(-))
    public void enterChatAlarmEvent(String userNum, String message){
        kafkaTemplate.send("enterChatAlarm-events", userNum+ "|" +message);
        System.out.println("Kafka 채팅 알림 이벤트 전송: " + message);
    }

    // chat-list-unread-update (send)
    public void chatRoomListUpdateEvent(String userNum, String message){
        kafkaTemplate.send("chatRoomList-events", userNum+ "|" +message);
        System.out.println("Kafka 채팅 목록 이벤트 전송: " + message);
    }
}


