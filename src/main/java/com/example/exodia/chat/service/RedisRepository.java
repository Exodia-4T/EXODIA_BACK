package com.example.exodia.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class RedisRepository {
    private static final String ENTER_INFO = "ENTER_INFO";
    private static final String USER_INFO = "USER_INFO";

    /**
     * "ENTER_INFO", roomId, userId (유저가 입장한 채팅방 정보)
     */
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Long> chatRoomInfo;

    /**
     * 상대 정보는 sessionId 로 저장, 나의 정보는 userId에 저장
     * "USER_INFO", sessionId, userId
     */
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> userInfo;

//    @Resource(name = "redisTemplate")
//    private HashOperations<String, Long, Long> test;
//
//    public void putTest() {
//        test.put("test", 1L, 1L);
//    }
//
//    public void getTest() {
//        test.get("test", 1L);
//    }
//
//    public void delTest() {
//        test.delete("test", 1L);
//    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void userEnterRoomInfo(String userNum, Long chatRoomId) {
        chatRoomInfo.put(ENTER_INFO, userNum, chatRoomId);
    }

    // 사용자가 채팅방에 입장해 있는지 확인
    public boolean existChatRoomUserInfo(String userNum) {
        return chatRoomInfo.hasKey(ENTER_INFO, userNum);
    }

    // 사용자가 특정 채팅방에 입장해 있는지 확인
    public boolean existUserRoomInfo(Long chatRoomId, String userNum) {
        return getUserEnterRoomId(userNum).equals(chatRoomId);
    }

    // 사용자가 입장해 있는 채팅방 ID 조회
    public Long getUserEnterRoomId(String userNum) {
        return chatRoomInfo.get(ENTER_INFO, userNum);
    }

    // 사용자가 입장해 있는 채팅방 ID 조회
    public void exitUserEnterRoomId(String userNum) {
        chatRoomInfo.delete(ENTER_INFO, userNum);
    }

    // 나의 대화상대 정보 저장
    public void saveMyInfo(String sessionId, String userNum) {
        userInfo.put(USER_INFO, sessionId, userNum);
    }

    public boolean existMyInfo(String sessionId) {
        return userInfo.hasKey(USER_INFO, sessionId);
    }

    public String getMyInfo(String sessionId) {
        return userInfo.get(USER_INFO, sessionId);
    }

    // 나의 대화상대 정보 삭제
    public void deleteMyInfo(String sessionId) {
        userInfo.delete(USER_INFO, sessionId);
    }

}
