package com.example.exodia.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class UploadAwsFileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.expiration-time:3600}")
    private long expirationTime; // Presigned URL 만료 시간 (초 단위)

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final CommonMethod commonMethod;

    @Autowired
    public UploadAwsFileService(S3Client s3Client, S3Presigner s3Presigner, CommonMethod commonMethod) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.commonMethod = commonMethod;
    }

    // 다중 파일 업로드 메서드
    public List<String> uploadMultipleFilesAndReturnPaths(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();
        List<String> fileNames = new ArrayList<>(); // Pre-signed URL 생성을 위한 파일 이름 저장

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty()) {
                    System.out.println("빈 파일이므로 업로드 건너뜁니다. 파일 이름: " + file.getOriginalFilename());
                    continue;
                }

                String originalFileName = file.getOriginalFilename();
                String fileName = getUniqueFileName(originalFileName);

                byte[] fileData = file.getBytes();

                if (!commonMethod.fileSizeCheck(file)) {
                    throw new IllegalArgumentException("파일의 크기가 너무 큽니다: " + fileName);
                }

                // S3에 파일 업로드
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

                // 업로드된 파일의 이름을 저장하여 Pre-signed URL 생성을 위해 사용
                fileNames.add(fileName);

            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생: " + (file != null ? file.getOriginalFilename() : "null"), e);
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 중 알 수 없는 오류 발생: " + (file != null ? file.getOriginalFilename() : "null"), e);
            }
        }

        // Pre-signed URL 생성
        fileUrls.addAll(generatePresignedUrls(fileNames));

        return fileUrls;
    }

    // Pre-signed URL 생성 메서드
    public List<String> generatePresignedUrls(List<String> fileNames) {
        List<String> presignedUrls = new ArrayList<>();

        for (String fileName : fileNames) {
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirationTime))
                    .getObjectRequest(b -> b.bucket(bucket).key(fileName))
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            presignedUrls.add(presignedRequest.url().toString());
        }

        return presignedUrls;
    }

    // S3에서 파일 이름이 중복되는지 확인하고 중복 시 파일 이름에 숫자를 붙임
    private String getUniqueFileName(String originalFileName) {
        String baseName = getBaseName(originalFileName);
        String extension = getExtension(originalFileName);

        String fileName = baseName + extension;
        int count = 1;

        // 파일이 존재하는지 확인, 중복이면 숫자 추가
        while (doesFileExistInS3(fileName)) {
            fileName = baseName + " (" + count + ")" + extension;
            count++;
        }

        return fileName;
    }

    // 파일이 S3에 존재하는지 확인
    private boolean doesFileExistInS3(String fileName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build());
            return true; // 파일이 존재함
        } catch (NoSuchKeyException e) {
            return false; // 파일이 존재하지 않음
        }
    }

    // 파일 확장자를 추출하는 메서드
    private String getExtension(String originalFileName) {
        if (originalFileName != null && originalFileName.contains(".")) {
            return originalFileName.substring(originalFileName.lastIndexOf("."));
        } else {
            return "";
        }
    }

    // 파일 이름에서 확장자를 제외한 부분 추출
    private String getBaseName(String originalFileName) {
        if (originalFileName != null && originalFileName.contains(".")) {
            return originalFileName.substring(0, originalFileName.lastIndexOf("."));
        } else {
            return originalFileName; // 확장자가 없는 경우 원래 이름 반환
        }
    }
}
