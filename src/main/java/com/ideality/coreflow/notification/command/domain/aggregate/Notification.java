package com.ideality.coreflow.notification.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "target_type")
    @Enumerated(EnumType.STRING)
    private TargetType targetType;


    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "dispatch_at", nullable = false)
    private LocalDateTime dispatchAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_auto_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isAutoDelete;

}
