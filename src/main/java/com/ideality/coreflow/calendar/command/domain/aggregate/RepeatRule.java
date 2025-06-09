package com.ideality.coreflow.calendar.command.domain.aggregate;

import com.ideality.coreflow.calendar.command.application.dto.FrequencyInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime endDate;

    @Column(name = "by_day")
    private String byDay;

    @Column(name = "by_month_day")
    private Integer byMonthDay;

    @Column(name = "by_set_pos")
    private Integer bySetPos;

    public void updateRepeatRule(FrequencyInfo requestDTO) {
        this.scheduleId = requestDTO.getScheduleId();
        this.repeatInterval = requestDTO.getRepeatInterval();
        this.frequency = requestDTO.getFrequencyType();
        this.endDate = requestDTO.getEndDate();
        this.byDay = requestDTO.getByDay();
        this.byMonthDay = requestDTO.getByMonthDay();
        this.bySetPos = requestDTO.getBySetPos();
    }
}
