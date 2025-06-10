package com.ideality.coreflow.notification.command.application.service.impl;


import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationRecipient;
import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import com.ideality.coreflow.notification.command.domain.aggregate.TargetType;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRecipientRepository;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    @Override
    public void sendNotification(Long userId, String content, Long targetId, TargetType targetType) {
        // 알림 객체 생성
        Notification notification = Notification.builder()
                .targetType(targetType)  // 동적으로 전달받은 targetType 사용
                .targetId(targetId)      // 사건이나 프로젝트 ID를 targetId로 설정
                .content(content)        // 알림 내용
                .status(Status.SENT)     // 알림 상태 기본값
                .dispatchAt(LocalDateTime.now())  // 바로 보내기 위해 현재 시간으로 설정
                .createdAt(LocalDateTime.now())   // 생성 시간
                .isAutoDelete(false)     // 자동 삭제 여부
                .build();

        // 알림 저장
        notificationRepository.save(notification);

        // NotificationRecipient 객체 생성 (수신자)
        NotificationRecipient recipient = NotificationRecipient.builder()
                .notification(notification)  // 저장된 Notification 객체와 연결
                .userId(userId)              // 수신자 userId
                .build();

        // 수신자 저장
        notificationRecipientRepository.save(recipient);
    }


    @Override
    @Transactional
    public Long createDetailNotification(Long detailId, String content) {
        Notification notification = Notification.builder()
                .targetType(TargetType.WORK)
                .targetId(detailId)
                .content(content)
                .status(Status.SENT)
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
                .targetType(TargetType.WORK)
                .targetId(taskId)
                .content(content)
                .status(Status.SENT)
                .dispatchAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        return notification.getId();
    }
}
