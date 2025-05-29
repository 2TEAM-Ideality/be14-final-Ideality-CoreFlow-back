package com.ideality.coreflow.payment.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delay_payment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class DelayPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delay_days")
    private int delayDays;

    @Column(name = "action_detail")
    private String actionDetail;

    @Column(name = "delay_reason_id")
    private Long delayReasonId;

    @Column(name = "payment_id")
    private Long paymentId;
}
