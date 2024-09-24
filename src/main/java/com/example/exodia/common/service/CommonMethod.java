package com.example.exodia.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CommonMethod {

    // 파일 크기 제한을 설정할 수 있는 기본 최대 파일 크기 (1GB로 설정)
    private static final long MAX_FILE_SIZE = 1L * 1024 * 1024 * 1024; // 1GB

    public Boolean fileSizeCheck(MultipartFile file) {
        // 파일 크기가 제한을 넘지 않는지 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean fileSizeCheckFromByte(String fileName, byte[] file) {
        // 바이트 배열로 파일 크기를 확인
        if (file.length > MAX_FILE_SIZE) {
            return false;
        } else {
            return true;
        }
    }

    // 파일 확장자에 관계없이 사용할 수 있는 최대 파일 크기
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}
