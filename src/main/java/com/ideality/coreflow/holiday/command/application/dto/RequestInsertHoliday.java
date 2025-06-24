package com.ideality.coreflow.holiday.command.application.dto;

import com.ideality.coreflow.holiday.command.domain.aggregate.HolidayType;
import com.ideality.coreflow.holiday.command.domain.aggregate.RepeatType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RequestInsertHoliday {
    String name;
    LocalDate date;
    HolidayType holidayType;
    RepeatType repeatType;
}
