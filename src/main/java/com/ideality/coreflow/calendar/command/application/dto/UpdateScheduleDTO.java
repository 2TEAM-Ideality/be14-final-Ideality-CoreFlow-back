package com.ideality.coreflow.calendar.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateScheduleDTO {
    // 개인 일정 수정

    private Long scheduleId;
    private Long createdBy;

    private String name;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String eventType;

    private Boolean isRepeat;
    private FrequencyInfo frequencyInfo;

    private List<LocalDateTime> reminder;  // 리마인더
}
