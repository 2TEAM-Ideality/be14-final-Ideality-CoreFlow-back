package com.ideality.coreflow.notification.command.application.service;

public interface NotificationService {
    Long createDetailNotification(Long detailId, String content);

    Long createMentionNotification(Long taskId, String content);
}
