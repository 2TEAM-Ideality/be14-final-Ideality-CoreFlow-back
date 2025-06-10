package com.ideality.coreflow.notification.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.notification.command.application.service.NotificationRecipientsService;
import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationRecipient;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRecipientsRepository;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationRecipientsServiceImpl implements NotificationRecipientsService {

    private final NotificationRecipientsRepository notificationRecipientsRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void createRecipients(Map<Long, List<Long>> notificationIdToUserIds) {

        for (Map.Entry<Long, List<Long>> entry : notificationIdToUserIds.entrySet()) {
            Long notificationId = entry.getKey();
            List<Long> recipientIds = entry.getValue();

            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));

            for (Long recipientId : recipientIds) {
                log.info("Creating recipient {} for notification {}", recipientId, notificationId);

                NotificationRecipient notificationRecipient = NotificationRecipient.builder()
                        .notification(notification)
                        .userId(recipientId)
                        .build();

                notificationRecipientsRepository.save(notificationRecipient);
            }
        }
    }

    @Override
    @Transactional
    public void createRecipientsByMention(List<Long> userIdByMention, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOTIFICATION_NOT_FOUND));

        for (Long userId : userIdByMention) {
            log.info("Creating recipient {} for notification {}", userId, notificationId);

            NotificationRecipient notificationRecipient = NotificationRecipient.builder()
                    .notification(notification)
                    .userId(userId)
                    .build();

            notificationRecipientsRepository.save(notificationRecipient);
        }
    }
}