package com.ideality.coreflow.notification.command.application.service;

import java.util.List;

public interface NotificationService {
    Long createDetailNotification(Long detailId);

    Long createMentionNotification(Long taskId);
}
