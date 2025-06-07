package com.ideality.coreflow.notification.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = true)
    private String content;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'PENDING'")
    private String status;

    @Column(name = "dispatch_at", nullable = false)
    private LocalDateTime dispatchAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @Column(name = "is_auto_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAutoDelete;

    // 추가된 Validation (Enum 사용)
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    // Enum을 사용할 경우, status 및 targetType을 Enum으로 지정하면 추가적인 타입 안전성을 확보할 수 있음
    public enum TargetType {
        APPROVAL, PROJECT, WORK, SCHEDULE
    }

    public enum Status {
        PENDING, SENT, READ
    }
}
