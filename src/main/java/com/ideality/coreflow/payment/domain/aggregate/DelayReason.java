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
public class DelayReason {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;
}
