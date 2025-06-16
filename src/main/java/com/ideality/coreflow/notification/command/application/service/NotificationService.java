package com.ideality.coreflow.notification.command.application.service;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.TargetType;


public interface NotificationService {

    void sendNotification(Long userId, String content, Long targetId, TargetType targetType);

    Long createDetailNotification(Long detailId, String content);

    Long createMentionNotification(Long taskId, String content);

    void save(Notification notification);

    Long createInviteProject(Long projectId, String content);
}
