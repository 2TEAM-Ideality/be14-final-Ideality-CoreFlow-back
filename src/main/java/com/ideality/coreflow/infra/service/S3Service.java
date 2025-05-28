package com.ideality.coreflow.infra.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class S3Service {
    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String folder) {
        /* 설명. uuid로 파일명을 충돌나지 않게 수정 */
        String fileName = generateFileName(file);           // uuid.jpg
        String key = generateKey(fileName, folder);          // profile-image/uuid.jpg
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
            );

            return amazonS3Client.getUrl(bucketName, key).toString();  // ✅ key로 URL 구성
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
    }

    private String generateKey(String fileName, String folder) {
        return folder + "/" + fileName;
    }

    // json 업로더
    public String uploadJson(String jsonContent, String folder, String fileName) {
        String key = generateKey(fileName, folder);
        byte[] contentBytes = jsonContent.getBytes(StandardCharsets.UTF_8);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(contentBytes.length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes);

        amazonS3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));

        return amazonS3Client.getUrl(bucketName, key).toString();  // 전체 URL 반환
    }

}
