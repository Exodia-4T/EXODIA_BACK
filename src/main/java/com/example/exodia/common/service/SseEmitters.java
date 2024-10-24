package com.example.exodia.common.service;

import com.example.exodia.chat.dto.ChatAlarmResponse;
import com.example.exodia.notification.domain.Notification;
import com.example.exodia.notification.dto.NotificationDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SseEmitters {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final int MAX_SSE_CONNECTIONS = 3;

    public SseEmitter addEmitter(String userNum) {
        SseEmitter emitter = new SseEmitter(600_000L);
        emitters.put(userNum, emitter);
        System.out.println("SSE Emitter 추가: " + userNum);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (IOException e) {
                emitters.remove(userNum);
                System.out.println("SSE 연결 오류 발생: " + userNum);
            }
        }, 0, 30, TimeUnit.SECONDS);

        // SSE 종료/에러 -> 백업처리로직
        emitter.onCompletion(() -> emitters.remove(userNum));
        emitter.onTimeout(() -> {
            emitters.remove(userNum);
            System.out.println("SSE 연결 타임아웃 발생: " + userNum);
        });
        emitter.onError(e -> {
            emitters.remove(userNum);
            System.out.println("SSE 연결 오류 발생: " + userNum);

        });

//        try {
//            emitter.send(SseEmitter.event().name("connect").data("connected!"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return emitter;
    }

    // 모든 사용자
    public void sendToAll(Notification notification) {
        emitters.forEach((userNum, emitter) -> {
            try {
                emitter.send(SseEmitter.event().data(notification));
            } catch (IOException e) {
                emitters.remove(userNum);
            }
        });
    }

    public void sendToUser(String userNum, NotificationDTO dto) {
        SseEmitter emitter = emitters.get(userNum);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(dto));
                System.out.println("알림 전송 성공: " + userNum);
            } catch (IOException e) {
                emitters.remove(userNum);
                System.out.println("알림 전송 실패, SSE 연결 해제: " + userNum);
                e.printStackTrace();
            }
        } else {
            System.out.println("SSE 연결 없음: " + userNum);
        }
    }

    public void sendChatToUser(String userNum, ChatAlarmResponse dto){
        SseEmitter emitter = emitters.get(userNum);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().data(dto));
                System.out.println("알림 전송 성공: " + userNum);
            } catch (IOException e) {
                emitters.remove(userNum);
                System.out.println("알림 전송 실패, SSE 연결 해제: " + userNum);
                e.printStackTrace();
            }
        } else {
            System.out.println("SSE 연결 없음: " + userNum);
        }
    }
}

