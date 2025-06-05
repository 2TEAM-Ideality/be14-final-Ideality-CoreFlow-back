package com.ideality.coreflow.approval.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Approval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String title;

    @Enumerated(EnumType.STRING)
    private ApprovalType type;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "work_id")
    private Long workId;

    @Column(name = "reject_reason")
    private String rejectReason;

    public void updateStatus(ApprovalStatus status) {
        this.status = status;
        this.approvedAt = LocalDateTime.now();
    }

    public void updateReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
