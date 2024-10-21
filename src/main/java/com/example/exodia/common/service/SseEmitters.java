package com.example.exodia.common.service;

import com.example.exodia.notification.domain.Notification;
import com.example.exodia.notification.dto.NotificationDTO;
import com.example.exodia.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//@Service
@Service("myKafkaConsumer")
public class SseEmitters {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String userNum) {
        SseEmitter emitter = new SseEmitter(360_000L);
        emitters.put(userNum, emitter);
        System.out.println("SSE Emitter 추가: " + userNum);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));  // 주기적으로 heartbeat 전송
            } catch (IOException e) {
                emitters.remove(userNum);
                System.out.println("SSE 연결 오류 발생: " + userNum);
            }
        }, 0, 10, TimeUnit.SECONDS);

        // SSE 종료/에러 -> 백업처리로직
        emitter.onCompletion(() -> emitters.remove(userNum));
        emitter.onTimeout(() -> {
            emitters.remove(userNum);
            System.out.println("SSE 연결 타임아웃 발생: " + userNum);
        });
        emitter.onError(e -> {
            emitters.remove(userNum);
            });

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
}

