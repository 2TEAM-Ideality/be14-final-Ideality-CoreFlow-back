package com.ideality.coreflow.notification.command.domain.repository;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
