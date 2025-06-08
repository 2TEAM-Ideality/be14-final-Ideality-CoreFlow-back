package com.ideality.coreflow.notification.command.application.service;

import java.util.List;
import java.util.Map;

public interface NotificationRecipientsService {
    void createRecipients(Map<Long, List<Long>> notificationIdToUserIds);
}
