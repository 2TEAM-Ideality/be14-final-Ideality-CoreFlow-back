package com.ideality.coreflow.calendar.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "repeat_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepeatRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키이지만 단순 ID로만 사용
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @Column(name = "repeat_interval", nullable = false)
    private Integer repeatInterval = 1;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "by_day")
    private String byDay;

    @Column(name = "by_month_day")
    private Integer byMonthDay;

    @Column(name = "by_set_pos")
    private Integer bySetPos;
}
