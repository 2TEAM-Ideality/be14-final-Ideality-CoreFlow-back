package com.ideality.coreflow.notification.command.application.dto;

import com.ideality.coreflow.notification.command.domain.aggregate.Status;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

// 알림 데이터를 담는 DTO 클래스 (NotificationData)
@Getter
@Data
public class NotificationData {
    private final String content;
    private final String date;
    private final String status;
    private final Long id;  // ID 필드 추가
    private final Boolean isAutoDelete;
    private final Long targetId;
    private final NotificationTargetType targetType;

    // 생성자
    // 생성자: LocalDateTime과 Status를 String으로 변환
    public NotificationData(String content, LocalDateTime date, Status status, Long id, Boolean isAutoDelete,Long targetId,NotificationTargetType targetType) {
        this.content = content;
        this.date = date.toString();  // LocalDateTime을 String으로 변환
        this.status = status.toString();  // Status를 String으로 변환
        this.id = id;
        this.isAutoDelete = isAutoDelete;
        this.targetId = targetId;  // targetId 필드 초기화
        this.targetType = targetType;
    }

}