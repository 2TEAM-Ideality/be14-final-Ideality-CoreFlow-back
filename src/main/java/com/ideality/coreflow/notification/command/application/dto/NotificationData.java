package com.ideality.coreflow.notification.command.application.dto;

import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import lombok.Getter;

import java.time.LocalDateTime;

// 알림 데이터를 담는 DTO 클래스 (NotificationData)
@Getter
public class NotificationData {
    private final String content;
    private final String date;
    private final String status;
    private final Long id;  // ID 필드 추가

    // 생성자
    // 생성자: LocalDateTime과 Status를 String으로 변환
    public NotificationData(String content, LocalDateTime date, Status status, Long id) {
        this.content = content;
        this.date = date.toString();  // LocalDateTime을 String으로 변환
        this.status = status.toString();  // Status를 String으로 변환
        this.id = id;
    }

}