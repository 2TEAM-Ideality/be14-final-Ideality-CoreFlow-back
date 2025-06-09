package com.ideality.coreflow.notification.query.controller;

import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.notification.query.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    // SLF4J 로거 선언
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    @GetMapping("/api/notifications/stream")
    public SseEmitter streamNotifications() {
        SseEmitter emitter = new SseEmitter();
        // 이미 보낸 알림을 추적하기 위한 Set (중복 알림을 방지)
        Set<Long> sentNotifications = new HashSet<>();

        // 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        // 비동기 방식으로 알림을 전송
        new Thread(() -> {
            try {
                // 테넌트 정보를 새로 생성된 스레드에 전파
                String tenant = TenantContext.getTenant();  // 현재 테넌트 정보 가져오기
                TenantContext.setTenant("company_a");  // 새 테넌트 설정

                // 로그 찍어보기
                logger.info("Authenticated User ID: {}", userId);
                logger.info("테넌트: " + tenant);
                logger.info("새 테넌트: " + TenantContext.getTenant());

                while (true) {
                    // 알림을 가져와서 전송
                    notificationQueryService.getMyNotifications(userId)
                            .forEach(notification -> {
                                // 이미 보낸 알림은 제외
                                if (!sentNotifications.contains(notification.getId())) {
                                    try {
                                        // 알림 전송
                                        emitter.send(SseEmitter.event().data(notification.getContent()));
                                        // 보낸 알림을 sentNotifications에 추가
                                        sentNotifications.add(notification.getId());
                                    } catch (IOException e) {
                                        emitter.completeWithError(e);  // 오류 발생 시 처리
                                    }
                                }
                            });

                    // 5초마다 알림 확인
                    try {
                        TimeUnit.SECONDS.sleep(5);  // 5초마다 알림 확인
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        emitter.completeWithError(e);
                        break;
                    }
                }
            } finally {
                // 스레드 종료 시 테넌트 정보 초기화
                TenantContext.clear();  // 스레드 종료 시 테넌트 정보 삭제
            }
        }).start();

        return emitter;
    }
}
