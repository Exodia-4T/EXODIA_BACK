package com.example.exodia.notification.service;

import com.example.exodia.common.service.SseEmitters;
import com.example.exodia.notification.domain.NotificationType;
import com.example.exodia.notification.dto.NotificationDTO;
import com.example.exodia.user.domain.User;
import com.example.exodia.user.repository.UserRepository;
import com.example.exodia.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ObjectStreamClass;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {
//    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> notificationRedisTemplate;
    private final SseEmitters sseEmitters;

    @Autowired
    public NotificationService(UserRepository userRepository, @Qualifier("notification") RedisTemplate<String, Object> notificationRedisTemplate,
                               SseEmitters sseEmitters) {
        this.userRepository = userRepository;
        this.notificationRedisTemplate = notificationRedisTemplate;
        this.sseEmitters = sseEmitters;
    }


//    public List<NotificationDTO> getNotificationsByUser(String userNum) {
//
//        User user = userRepository.findByUserNum(userNum)
//                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));
//
//        // 해당 유저의 알림 리스트를 가져옴
//        List<Notification> notifications = notificationRepository.findByUserId(user.getId());
//
//        // Notification 엔티티를 DTO로 변환하여 반환
//        return notifications.stream()
//                .map(NotificationDTO::fromEntity)  // Notification -> NotificationDTO 변환
//                .collect(Collectors.toList());
//    }
    /* kafka - > redis */
@Transactional
    public void saveNotification(String userNum, NotificationDTO notificationDTO) {
        if (notificationDTO.getId() == null) {
            notificationDTO.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        }
        String redisKey = "notifications:" + userNum;
        notificationDTO.setRead(false);
        notificationRedisTemplate.opsForHash().put(redisKey, notificationDTO.getId().toString(), notificationDTO);
        notificationRedisTemplate.expire(redisKey, Duration.ofDays(3)); // 알림 TTL 설정
        sseEmitters.sendToUser(userNum, notificationDTO);
    }

    /* 사용자 조회 */
//    public List<NotificationDTO> getNotifications(String userNum) {
//        String redisKey = "notifications:" + userNum;
//        Map<Object, Object> notifications = notificationRedisTemplate.opsForHash().entries(redisKey);
//        return notifications.values().stream()
//                .map(obj -> (NotificationDTO) obj)
//                .collect(Collectors.toList());
//    }

    /* 사용자 조회 */
    public List<NotificationDTO> getNotifications(String userNum) {
        String redisKey = "notifications:" + userNum;
        Map<Object, Object> notifications = notificationRedisTemplate.opsForHash().entries(redisKey);

        List<NotificationDTO> notificationList = notifications.values().stream()
                .map(obj -> (NotificationDTO) obj)
                .map(notification -> {
                    if (notification.getNotificationTime() == null) {
                        notification.setNotificationTime(LocalDateTime.now());
                    }
                    if (!notification.isRead()) {
                        notification.setRead(true);
                        notificationRedisTemplate.opsForHash().put(redisKey, notification.getId().toString(), notification);
                    }
                    return notification;
                })
                .sorted((a, b) -> b.getNotificationTime().compareTo(a.getNotificationTime()))
                .collect(Collectors.toList());

        return notificationList;
    }


    /* 읽음 처리 */
    @Transactional
    public void markNotificationAsRead(String userNum, String notificationId) {
        String redisKey = "notifications:" + userNum;
        NotificationDTO notification = (NotificationDTO) notificationRedisTemplate.opsForHash().get(redisKey, notificationId);

        if (notification != null) {
            notification.setRead(true);
            notificationRedisTemplate.opsForHash().put(redisKey, notificationId, notification);
            notificationRedisTemplate.expire(redisKey, Duration.ofDays(3)); // TTL 갱신
        } else {
            System.out.println("알림을 찾을 수 없어서 읽음 처리되지 않았습니다.");
        }
    }


    public boolean isNotificationRead(String userNum, String notificationId) {
        String redisKey = "notifications:" + userNum;
        NotificationDTO notificationDTO = (NotificationDTO) notificationRedisTemplate.opsForHash().get(redisKey, notificationId);

        if (notificationDTO != null) {
            return notificationDTO.isRead();
        } else {
            System.out.println("알림을 찾을 수 없어서 읽음 여부를 확인할 수 없습니다.");
            return false;
        }
    }



//    // 읽지 않은 알림의 개수를 반환
//    public long countUnreadNotifications(String userNum) {
//        User user = userRepository.findByUserNum(userNum)
//                .orElseThrow(() -> new IllegalArgumentException("해당 사번을 가진 유저가 없습니다."));
//
//        return notificationRepository.countByUserAndIsReadFalse(user);
//    }
//
//    // 특정 알림을 읽음 처리
//    @Transactional
//    public void markNotificationAsRead(Long notificationId) {
//        Notification notification = notificationRepository.findById(notificationId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));
//
//        // 알림의 소유자가 현재 사용자와 일치하는지 확인
//        String userNum = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (!notification.getUser().getUserNum().equals(userNum)) {
//            throw new SecurityException("다른 사용자의 알림을 읽음으로 처리할 수 없습니다.");
//        }
//
//        // 읽음 처리
//        notification.markAsRead();
//        notificationRepository.save(notification);
//    }

}
