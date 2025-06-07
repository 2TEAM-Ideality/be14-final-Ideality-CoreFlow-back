package com.ideality.coreflow.infra.s3;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Service
@SpringBootTest
public class S3ServiceTests {

    @Autowired
    private S3Service s3Service;

    @DisplayName("프로필 사진 업로드 테스트")
    @Test
    public void successUploadImage() throws IOException {
        // given
        String folder = "profile-image";
        String filename = "test-image.jpg";

        // classpath:resources/static/test-image.jpg 경로에 있어야 함
        ClassPathResource resource = new ClassPathResource("static/" + filename);
        MultipartFile multipartFile = new MockMultipartFile(
                filename,
                filename,
                "image/jpeg",
                resource.getInputStream()
        );

        // when
        String uploadedUrl = s3Service.uploadImage(multipartFile, folder);

        // then
        System.out.println("✅ 업로드 성공: " + uploadedUrl);
        assertThat(uploadedUrl).contains(folder + "/");
        assertThat(uploadedUrl).startsWith("https://");
    }
}
