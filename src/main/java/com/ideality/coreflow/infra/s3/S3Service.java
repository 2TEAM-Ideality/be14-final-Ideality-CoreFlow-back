package com.ideality.coreflow.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /* 설명. 프로필 이미지 만들기 */
    public String uploadImage(MultipartFile file, String folder) {
        /* 설명. uuid로 파일명을 충돌나지 않게 수정 */
        String fileName = generateFileName();           // uuid.jpg
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

    /* 설명. 한글이 들어가면 presigned-url을 써도 깨지게 되는 문제 발생(uuid로만) */
    public String generateFileName() {
        return UUID.randomUUID().toString();
    }

    private String generateKey(String fileName, String folder) {
        return folder + "/" + fileName;
    }

    /* JSON 업로더 */
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

    /* JSON 파일 다운로드 */
    public String getJsonFile(String url) {
        try {
            String key = extractS3KeyFromUrl(url);

            // S3 객체 가져오기
            var s3Object = amazonS3Client.getObject(bucketName, key);
            var inputStream = s3Object.getObjectContent();

            // 문자열로 읽기
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("S3 JSON 파일 다운로드 실패: {}", url, e);
            throw new RuntimeException("S3 JSON 파일 다운로드 실패", e);
        }
    }

    // S3 URL → key 추출 (경로만)
    public String extractS3KeyFromUrl(String url) {
        if (url == null || !url.contains(".com/")) {
            throw new IllegalArgumentException("S3 URL 형식이 잘못되었습니다: " + url);
        }
        return url.substring(url.indexOf(".com/") + 5);
    }

    /* 설명. 결재 서류 + 댓글 파일용 업로더 */
    public UploadFileResult uploadFile(MultipartFile file, String folder) {
        String originName = file.getOriginalFilename();             // 원본 파일명
        String storedName = generateFileName();                     // UUID 저장용 파일명
        String key = generateKey(storedName, folder);               // ex) comment-docs/uuid

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // ✅ 다운로드 시 원본 파일명으로 내려받게 설정
            String encodedFileName = java.net.URLEncoder.encode(originName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20"); // 공백 처리
            metadata.setContentDisposition("attachment; filename*=UTF-8''" + encodedFileName);

            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
            );

            String url = amazonS3Client.getUrl(bucketName, key).toString();

            return new UploadFileResult(
                    originName,
                    storedName,
                    url,
                    file.getContentType(),
                    file.getSize() + ""
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public byte[] getFileBytes(String key) {
        try {
            var s3Object = amazonS3Client.getObject(bucketName, key);
            return s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            log.error("파일 다운로드 실패 - key: {}", key, e);
            throw new RuntimeException("파일 다운로드 실패", e);
        }
    }
}
