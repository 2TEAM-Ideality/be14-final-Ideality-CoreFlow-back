package com.ideality.coreflow.notification.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.infra.tenant.config.TenantContext;

import com.ideality.coreflow.notification.command.application.dto.NotificationData;
import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import com.ideality.coreflow.notification.query.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

    // SLF4J 로거 선언
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    // 알림 조회 API
    @GetMapping("/api/notifications")
    public APIResponse<List<NotificationData>> getNotifications() {
        // SecurityContextHolder에서 인증된 사용자 ID를 가져옵니다.
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 사용자 ID를 기준으로 알림 조회
        List<Notification> notifications = notificationQueryService.getMyNotifications(userId);

        // 알림 데이터를 DTO로 변환
        List<NotificationData> notificationDataList = notifications.stream()
                .map(notification -> new NotificationData(notification.getContent(), notification.getDispatchAt(), notification.getStatus(), notification.getId()))
                .collect(Collectors.toList());

        // 조회된 알림을 반환
        return APIResponse.success(notificationDataList, "알림 조회 성공");
    }

    // 메시지를 클라이언트로 보내는 메서드
    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근
    public void sendNotification(Notification notification) {
        // 클라이언트에게 알림을 보냄
        messagingTemplate.convertAndSend("/topic/notifications", notification);  // /topic/notifications 경로로 메시지 전송
    }



    @PreAuthorize("isAuthenticated()")  // 인증된 사용자만 접근 가능
    @GetMapping(value = "/api/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam("token") String token) {
        // 로그에 token 값 출력
        logger.info("Received token: {}", token);

        if (token == null || token.isEmpty()) {
            logger.error("Token is null or empty!");
            return new SseEmitter(0L); // 적절한 처리 (토큰이 없으면 401)
        }

        SseEmitter emitter = new SseEmitter();

        // 별도의 스레드에서 비동기적으로 실시간 알림을 전송
        new Thread(() -> {
            try {
                // 1. 기존 테넌트 정보 가져오기
                String tenant = TenantContext.getTenant();  // 현재 테넌트 정보를 가져옵니다.
                // 2. 여기서 새로운 테넌트를 설정
                TenantContext.setTenant("company_a");  // 예시로 "company_a"로 테넌트 설정

                // 로그로 테넌트 정보 출력
                logger.info("Authenticated User ID: {}", SecurityContextHolder.getContext().getAuthentication().getName());
                logger.info("현재 테넌트: " + tenant);  // 이전 테넌트
                logger.info("새로 설정된 테넌트: " + TenantContext.getTenant());  // 새 테넌트

                // 사용자 ID 가져오기 (SecurityContext에서)
                Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

                while (true) {
                    // 새로운 알림 조회 (최신 알림을 확인)
                    List<Notification> notifications = notificationQueryService.getMyNotifications(userId);

                    // 알림이 있을 경우, 클라이언트로 전송
                    if (!notifications.isEmpty()) {
                        List<NotificationData> notificationDataList = notifications.stream()
                                .map(notification -> new NotificationData(notification.getContent(), notification.getDispatchAt(), notification.getStatus(), notification.getId()))
                                .collect(Collectors.toList());

                        emitter.send(SseEmitter.event().data(notificationDataList));
                    }

                    // 5초마다 알림을 전송
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (InterruptedException e) {
                emitter.completeWithError(e);
            } catch (Exception e) {
                emitter.completeWithError(e); // 예외 발생 시 처리
            }
        }).start();

        return emitter;
    }


}