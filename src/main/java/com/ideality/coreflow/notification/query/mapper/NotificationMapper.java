package com.ideality.coreflow.notification.query.mapper;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {
    // 사용자별 알림 가져오기
    List<Notification> getMyNotifications(Long userId);
    // 최신 알림 조회
    List<Notification> getLatestNotifications(@Param("userId") Long userId, @Param("lastNotificationId") Long lastNotificationId);
    // 사용자가 해당 알림을 받은 수신자인지 확인
    boolean isRecipient(Long notificationId, Long userId);
    // 알림 조회
    Notification getNotificationById(Long id);
}
