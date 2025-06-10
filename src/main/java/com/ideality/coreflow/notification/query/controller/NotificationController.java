package com.ideality.coreflow.notification.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.infra.tenant.config.TenantContext;

import com.ideality.coreflow.notification.command.application.dto.NotificationData;
import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import com.ideality.coreflow.notification.query.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    // SLF4J 로거 선언
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    @GetMapping(value ="/api/notifications/stream",produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam("token") String token) {
        SseEmitter emitter = new SseEmitter();
        Set<Long> sentNotifications = new HashSet<>();
        logger.info("Received token: {}", token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        // 비동기 방식으로 알림을 전송
        new Thread(() -> {
            try {
                String tenant = TenantContext.getTenant();
                TenantContext.setTenant("company_a");

                logger.info("Authenticated User ID: {}", userId);
                logger.info("테넌트: " + tenant);
                logger.info("새 테넌트: " + TenantContext.getTenant());

                while (true) {
                    // 알림을 가져와서 전송
                    List<NotificationData> notifications = notificationQueryService.getMyNotifications(userId)
                            .stream()
                            .filter(notification -> !sentNotifications.contains(notification.getId()))
                            .map(notification -> {
                                // NotificationData 객체로 알림을 반환
                                return new NotificationData(notification.getContent(), notification.getDispatchAt(), notification.getStatus(), notification.getId());
                            })
                            .collect(Collectors.toList());

                    // 여러 알림을 하나의 데이터 배열로 묶어서 보내기
                    if (!notifications.isEmpty()) {
                        APIResponse<List<NotificationData>> apiResponse = APIResponse.success(notifications, "알림 조회에 성공하셨습니다.");
                        try {
                            emitter.send(SseEmitter.event().data(apiResponse));
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }

                    // 보낸 알림을 sentNotifications에 추가
                    sentNotifications.addAll(notifications.stream()
                            .map(NotificationData::getId)  // 알림 객체에서 ID만 추출
                            .collect(Collectors.toSet()));

                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        emitter.completeWithError(e);
                        break;
                    }
                }
            } finally {
                TenantContext.clear();
            }
        }).start();

        return emitter;
    }

}