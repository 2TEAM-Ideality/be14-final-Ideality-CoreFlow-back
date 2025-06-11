package com.ideality.coreflow.notification.query.mapper;

import com.ideality.coreflow.notification.command.domain.aggregate.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    // 사용자별 알림 가져오기
    List<Notification> getMyNotifications(Long userId);
}
