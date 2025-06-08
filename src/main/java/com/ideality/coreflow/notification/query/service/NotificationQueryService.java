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


    public List<Notification> getMyNotifications() {

        // SecurityContext에서 userId를 추출
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        return notificationMapper.getMyNotifications(userId);
    }
}
