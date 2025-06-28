package com.ideality.coreflow.infra.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /* ✅ 파일 업로드 - 일반 이미지용 */
    public String uploadImage(MultipartFile file, String folder) {
        String storedName = generateSafeFileName(file); // 확장자 유지, 한글 제거
        String key = generateKey(storedName, folder);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getSafeContentType(file));
            metadata.setContentLength(file.getSize());

            amazonS3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
            return amazonS3Client.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    /* ✅ 파일 업로드 - 일반 파일용 */
    public UploadFileResult uploadFile(MultipartFile file, String folder) {
        String originName = file.getOriginalFilename();
        String storedName = generateSafeFileName(file);
        String key = generateKey(storedName, folder);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getSafeContentType(file));
            metadata.setContentLength(file.getSize());

            // ✅ 다운로드 시 원본 파일명 유지 (UTF-8 인코딩 처리)
            String encodedFileName = URLEncoder.encode(originName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            metadata.setContentDisposition("attachment; filename*=UTF-8''" + encodedFileName);

            amazonS3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
            String url = amazonS3Client.getUrl(bucketName, key).toString();

            return new UploadFileResult(
                    originName,
                    storedName,
                    url,
                    getSafeContentType(file),
                    String.valueOf(file.getSize())
            );
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    /* ✅ JSON 업로드 */
    public String uploadJson(String jsonContent, String folder, String fileName) {
        String key = generateKey(fileName, folder);
        byte[] bytes = jsonContent.getBytes(StandardCharsets.UTF_8);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(bytes.length);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        amazonS3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, metadata));

        return amazonS3Client.getUrl(bucketName, key).toString();
    }

    /* ✅ JSON 다운로드 */
    public String getJsonFile(String url) {
        try {
            String key = extractS3KeyFromUrl(url);
            var s3Object = amazonS3Client.getObject(bucketName, key);
            var inputStream = s3Object.getObjectContent();
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("S3 JSON 파일 다운로드 실패: {}", url, e);
            throw new RuntimeException("S3 JSON 파일 다운로드 실패", e);
        }
    }


    /* ✅ 파일 삭제 */
    public void deleteFileByUrl(String url) {
        try {
            String key = extractS3KeyFromUrl(url);
            amazonS3Client.deleteObject(bucketName, key);
            log.info("S3 파일 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", url, e);
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }

    // ✅ 안전한 파일명 생성 (UUID + 확장자만 유지, 한글 제거)
    public String generateSafeFileName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }

        return UUID.randomUUID().toString() + extension;
    }

    // ✅ S3 업로드 Key 생성
    private String generateKey(String fileName, String folder) {
        return folder + "/" + fileName;
    }

    // ✅ URL → S3 Key 추출
    public String extractS3KeyFromUrl(String url) {
        if (url == null || !url.contains(".com/")) {
            throw new IllegalArgumentException("S3 URL 형식이 잘못되었습니다: " + url);
        }
        return url.substring(url.indexOf(".com/") + 5);
    }

    // ✅ 안전한 MIME 타입 추출 (fallback 처리 포함)
    private String getSafeContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) return contentType;

        // fallback: 확장자 → MIME 매핑
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();
        }

        return EXTENSION_TO_MIME_TYPE.getOrDefault(extension, "application/octet-stream");
    }

//    public String getMimeTypeFromFilename(String filename) {
//        String ext = Optional.ofNullable(filename)
//                .filter(f -> f.contains("."))
//                .map(f -> f.substring(f.lastIndexOf('.') + 1).toLowerCase())
//                .orElse("");
//        return EXTENSION_TO_MIME_TYPE.getOrDefault(ext, MediaType.APPLICATION_OCTET_STREAM_VALUE);
//    }
//
    // ✅ 확장자 → MIME 매핑 테이블
    private static final Map<String, String> EXTENSION_TO_MIME_TYPE = new HashMap<>();
    static {
        EXTENSION_TO_MIME_TYPE.put("txt", "text/plain");
        EXTENSION_TO_MIME_TYPE.put("csv", "text/csv");
        EXTENSION_TO_MIME_TYPE.put("pdf", "application/pdf");
        EXTENSION_TO_MIME_TYPE.put("doc", "application/msword");
        EXTENSION_TO_MIME_TYPE.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        EXTENSION_TO_MIME_TYPE.put("xls", "application/vnd.ms-excel");
        EXTENSION_TO_MIME_TYPE.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        EXTENSION_TO_MIME_TYPE.put("png", "image/png");
        EXTENSION_TO_MIME_TYPE.put("jpg", "image/jpeg");
        EXTENSION_TO_MIME_TYPE.put("jpeg", "image/jpeg");
        EXTENSION_TO_MIME_TYPE.put("gif", "image/gif");
        EXTENSION_TO_MIME_TYPE.put("bmp", "image/bmp");
        EXTENSION_TO_MIME_TYPE.put("zip", "application/zip");
        EXTENSION_TO_MIME_TYPE.put("json", "application/json");
    }
}