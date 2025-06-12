package com.ideality.coreflow.notification.command.domain.repository;

import com.ideality.coreflow.notification.command.domain.aggregate.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    // 필요한 추가 메서드를 정의할 수 있습니다.
}