package com.ideality.coreflow.email.command.application.service.impl;

import com.ideality.coreflow.email.command.application.dto.UserLoginInfo;
import com.ideality.coreflow.email.command.application.service.EmailSendService;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendServiceImpl implements EmailSendService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmailUserLoginInfo(UserLoginInfo userLoginInfo) {
        try {
            log.info("회원가입 완료 메일 발송");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = "<div style='font-family:Arial,sans-serif;'>"
                    + "<h2>" + TenantContext.getTenant() + " 회원 정보</h2>"
                    + "<p>사번: " + userLoginInfo.getEmployeeNum() + "</p>"
                    + "<p>비밀번호: " + userLoginInfo.getPassword() + "</p>"
                    + "</div>";

            helper.setFrom("Core Flow <coreflow@gmail.com>");
            helper.setTo(userLoginInfo.getEmail());
            helper.setSubject("Core Flow 회원 정보");
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
