package com.ideality.coreflow.email.command.application.service.impl;

import com.ideality.coreflow.email.command.application.service.EmailSendService;
import com.ideality.coreflow.email.command.domail.aggregate.EmailType;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendServiceImpl implements EmailSendService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailType type, Map<String, String> data) {
        String subject = getSubject(type);
        String htmlBody = generateHtmlBody(type, data);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("Core Flow <coreflow@gmail.com>");
            helper.setTo(data.get("email"));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    private String generateHtmlBody(EmailType type, Map<String, String> data) {
        return switch (type) {
            case USER_LOGIN_INFO -> String.format(
                    """
                    <div style='font-family:Arial,sans-serif;'>
                        <h2>%s 로그인 정보</h2>
                        <p>사번: %s</p>
                        <p>비밀번호: %s</p>
                    </div>
                    """,
                    TenantContext.getTenant(),
                    data.get("employeeNum"),
                    data.get("password")
            );
            case VERIFICATION_CODE -> String.format(
                    """
                    <div style='font-family:Arial,sans-serif;'>
                        <h2>%s 비밀번호 변경 인증 코드</h2>
                        <p>인증 코드: %s</p>
                    </div>
                    """,
                    TenantContext.getTenant(),
                    data.get("verificationCode")
            );
            case NEW_PASSWORD -> String.format(
                    """
                    <div style='font-family:Arial,sans-serif;'>
                        <h2>%s 새 비밀번호<h2>
                        <p>새 비밀번호: %s</p>
                    </div>
                    """,
                    TenantContext.getTenant(),
                    data.get("password")
            );
        };
    }

    private String getSubject(EmailType type) {
        return switch (type) {
            case USER_LOGIN_INFO -> "Core Flow 회원 정보";
            case VERIFICATION_CODE -> "비밀번호 변경 인증 코드";
            case NEW_PASSWORD -> "새로운 비밀번호";
        };
    }

}
