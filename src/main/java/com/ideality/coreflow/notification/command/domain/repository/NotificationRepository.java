package com.ideality.coreflow.notification.command.domain.repository;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 필요한 추가 메서드를 정의할 수 있습니다.
}