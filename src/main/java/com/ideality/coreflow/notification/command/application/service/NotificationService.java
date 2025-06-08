package com.ideality.coreflow.notification.command.application.service;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationRecipient;
import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import com.ideality.coreflow.notification.command.domain.aggregate.TargetType;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRecipientRepository;
import com.ideality.coreflow.notification.command.domain.repository.NotificationRepository;
import com.ideality.coreflow.notification.query.mapper.NotificationMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    public NotificationService(NotificationRepository notificationRepository, NotificationRecipientRepository notificationRecipientRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }


    // 알림 전송 (특정 사용자에게)
    public void sendNotification(Long userId, String content, Long targetId, TargetType targetType) {
        // 알림 객체 생성
        Notification notification = Notification.builder()
                .targetType(targetType)  // 동적으로 전달받은 targetType 사용
                .targetId(targetId)      // 사건이나 프로젝트 ID를 targetId로 설정
                .content(content)        // 알림 내용
                .status(Status.PENDING)       // 알림 상태 기본값
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




}