package com.example.exodia.common.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UploadAwsFileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Client s3Client;
    private final CommonMethod commonMethod;

    @Autowired
    public UploadAwsFileService(S3Client s3Client, CommonMethod commonMethod) {
        this.s3Client = s3Client;
        this.commonMethod = commonMethod;
    }

    // 다중 파일 업로드 메서드
    public List<String> uploadMultipleFilesAndReturnPaths(List<MultipartFile> files) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // 파일이 비어 있는지 확인
                if (file.isEmpty()) {
                    System.out.println("빈 파일이므로 업로드 건너뜁니다. 파일 이름: " + file.getOriginalFilename());
                    continue;
                }

                // 파일 이름 설정
                String originalFileName = file.getOriginalFilename();
                final String fileName = (originalFileName == null || originalFileName.trim().isEmpty())
                        ? "default_" + System.currentTimeMillis()
                        : originalFileName;

                System.out.println("파일 이름 설정 완료: " + fileName);
                byte[] fileData = file.getBytes();
                System.out.println("파일 크기: " + file.getSize() + " bytes");

                // 파일 크기 체크
                if (!commonMethod.fileSizeCheck(file)) {
                    System.out.println("파일의 크기가 너무 큽니다: " + fileName);
                    throw new IllegalArgumentException("파일의 크기가 너무 큽니다: " + fileName);
                }

                // S3에 파일 업로드
                System.out.println("S3에 파일 업로드 시작: " + fileName);

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build();

                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

                // 업로드된 파일의 S3 URL 경로 반환
                String s3FilePath = s3Client.utilities()
                        .getUrl(a -> a.bucket(bucket).key(fileName))
                        .toExternalForm();

                System.out.println("S3 파일 URL: " + s3FilePath);

                fileUrls.add(s3FilePath);

            } catch (IOException e) {
                System.out.println("파일 업로드 중 IOException 발생: " + e.getMessage());
                throw new RuntimeException("파일 업로드 중 오류 발생: " + (file != null ? file.getOriginalFilename() : "null"), e);
            } catch (Exception e) {
                System.out.println("파일 업로드 중 알 수 없는 오류 발생: " + e.getMessage());
                throw new RuntimeException("파일 업로드 중 알 수 없는 오류 발생: " + (file != null ? file.getOriginalFilename() : "null"), e);
            }
        }

        System.out.println("모든 파일 업로드 완료. 업로드된 파일 목록: " + fileUrls);

        return fileUrls;
    }
}

