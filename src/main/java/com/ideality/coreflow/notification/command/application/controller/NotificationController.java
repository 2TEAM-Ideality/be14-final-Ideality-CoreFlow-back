package com.ideality.coreflow.notification.command.application.controller;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.query.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("notificationCommandController")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationService notificationService;

    @PatchMapping("/api/notifications/{id}")
    public ResponseEntity<APIResponse> deleteNotification(@PathVariable Long id) {
        // SecurityContextHolder에서 인증된 사용자 ID를 가져옵니다.
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        // 알림을 조회합니다.
        Notification notification = notificationQueryService.getNotificationById(id);

        // 알림이 존재하지 않으면 예외를 던집니다.
        if (notification == null) {
            throw new BaseException(ErrorCode.NOT_FOUND_NOTIFICATION);
        }

        // 알림을 받은 사용자가 본인인지 확인
        boolean isRecipient = notificationQueryService.isRecipient(notification.getId(), userId);
        if (!isRecipient) {
            throw new BaseException(ErrorCode.FORBIDDEN_NOTIFICATION);
        }

        // isAutoDelete를 true로 설정하여 삭제 상태로 변경
        notification.setIsAutoDelete(true);
        notificationService.save(notification);  // 알림 상태 저장

        // 성공적인 응답 반환
        return ResponseEntity.ok(APIResponse.success(null, "알림이 삭제되었습니다."));
    }

}
