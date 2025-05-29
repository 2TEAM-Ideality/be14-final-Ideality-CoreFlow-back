package com.ideality.coreflow.approval.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_participant")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ApprovalParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "approval_id")
    private Long approvalId;

    @Column(name = "user_id")
    private Long userId;

    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
