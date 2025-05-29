package com.ideality.coreflow.notification.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "dispatch_at", nullable = false)
    private LocalDateTime dispatchAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_auto_delete", nullable = false)
    private boolean isAutoDelete = false;

    public enum TargetType {
        PAYMENT, PROJECT, WORK, SCHEDULE
    }

    public enum NotificationStatus {
        PENDING, SENT, READ
    }
}
