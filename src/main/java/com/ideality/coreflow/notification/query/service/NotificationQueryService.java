package com.ideality.coreflow.notification.query.service;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import com.ideality.coreflow.notification.query.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationMapper notificationMapper;


    public List<Notification> getMyNotifications(Long userId) {

        return notificationMapper.getMyNotifications(userId);
    }
}
