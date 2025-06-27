package com.ideality.coreflow.notification.query.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NotificationDTO {

    private Long id;
    private String targetType;
    private Long targetId;
    private String content;
    private String status;
    private LocalDate dispatchAt;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Boolean isAutoDelete;
}