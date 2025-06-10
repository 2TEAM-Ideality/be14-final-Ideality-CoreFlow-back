package com.ideality.coreflow.notification.command.application.dto;

import lombok.*;

// 알림 객체를 감싸는 클래스
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
public class NotificationDTO{
    private String message;
    private String date;
    private String status;

}