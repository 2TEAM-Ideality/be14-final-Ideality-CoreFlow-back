package com.ideality.coreflow.notification.query.service;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.query.dto.NotificationDTO;
import com.ideality.coreflow.notification.query.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationMapper notificationMapper;

    public List<Notification> getMyNotifications(Long userId) {

        return notificationMapper.getMyNotifications(userId);
    }

    public List<Notification> getLatestNotifications(Long userId, Long lastNotificationId) {
        return notificationMapper.getLatestNotifications(userId, lastNotificationId);
    }

    // MyBatis로 알림 조회
    public Notification getNotificationById(Long id) {
        return notificationMapper.getNotificationById(id);
    }

    // MyBatis로 해당 알림을 받은 사용자인지 확인
    public boolean isRecipient(Long notificationId, Long userId) {
        return notificationMapper.isRecipient(notificationId, userId);
    }
    // 로그인한 사용자의 읽지 않은 결재 알림 전체 조회
    public List<NotificationDTO> getUnreadApprovalNotifications(Long userId) {
        return notificationMapper.selectUnreadApprovalNotifications(userId);
    }

}
