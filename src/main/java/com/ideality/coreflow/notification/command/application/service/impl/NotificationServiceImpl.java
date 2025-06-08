package com.ideality.coreflow.notification.command.application.service.impl;

import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Long createDetailNotification(Long detailId) {
        Notification notification = Notification.builder()
                .targetType(String.valueOf(Notification.TargetType.WORK))
                .targetId(detailId)
                .content("세부 일정에 태그 하였습니다.")
                .status(String.valueOf(Notification.Status.SENT))
                .dispatchAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        return notification.getId();
    }
}
