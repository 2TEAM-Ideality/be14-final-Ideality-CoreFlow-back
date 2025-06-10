package com.ideality.coreflow.notification.command.application.service.impl;

import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Long createDetailNotification(Long detailId, String content) {
        Notification notification = Notification.builder()
                .targetType(String.valueOf(Notification.TargetType.WORK))
                .targetId(detailId)
                .content(content)
                .status(String.valueOf(Notification.Status.SENT))
                .dispatchAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        return notification.getId();
    }

    @Override
    @Transactional
    public Long createMentionNotification(Long taskId, String content) {
        Notification notification = Notification.builder()
                .targetType(String.valueOf(Notification.TargetType.WORK))
                .targetId(taskId)
                .content(content)
                .status(String.valueOf(Notification.Status.SENT))
                .dispatchAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        return notification.getId();
    }

    @Override
    @Transactional
    public Long createInviteProject(Long projectId, String content) {
        Notification notification = Notification.builder()
                .targetType(String.valueOf(Notification.TargetType.PROJECT))
                .targetId(projectId)
                .content(content)
                .status(String.valueOf(Notification.Status.SENT))
                .dispatchAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        return notification.getId();
    }
}
