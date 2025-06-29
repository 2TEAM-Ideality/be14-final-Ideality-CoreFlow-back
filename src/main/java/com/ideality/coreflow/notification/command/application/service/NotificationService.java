package com.ideality.coreflow.notification.command.application.service;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType;


public interface NotificationService {

    void sendNotification(Long userId, String content, Long targetId, NotificationTargetType targetType);

    Long createDetailNotification(Long detailId, String content);

    Long createMentionNotification(Long taskId, String content);

    void save(Notification notification);

    Long createInviteProject(Long projectId, String content);

    boolean markNotificationAsRead(Long notificationId);

    Long createInviteTask(Long taskId, String content);
}
