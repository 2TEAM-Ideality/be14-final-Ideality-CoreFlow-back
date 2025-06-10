package com.ideality.coreflow.notification.command.domain.repository;

import com.ideality.coreflow.notification.command.domain.aggregate.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRecipientsRepository extends JpaRepository<NotificationRecipient, Long> {
}
