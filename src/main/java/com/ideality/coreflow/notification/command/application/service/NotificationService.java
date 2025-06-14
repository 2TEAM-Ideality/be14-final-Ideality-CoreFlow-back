package com.ideality.coreflow.notification.command.application.service;

import com.ideality.coreflow.notification.command.domain.aggregate.TargetType;


public interface NotificationService {

    void sendNotification(Long userId, String content, Long targetId, TargetType targetType);

    Long createDetailNotification(Long detailId, String content);

    Long createMentionNotification(Long taskId, String content);

    Long createInviteProject(Long projectId, String content);
}
