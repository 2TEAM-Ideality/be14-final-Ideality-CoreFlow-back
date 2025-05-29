package com.ideality.coreflow.payment.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_participant")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PaymentParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "user_id")
    private Long userId;

    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
