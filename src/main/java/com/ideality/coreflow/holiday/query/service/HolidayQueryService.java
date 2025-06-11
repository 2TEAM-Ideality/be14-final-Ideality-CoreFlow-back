package com.ideality.coreflow.holiday.query.service;

import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.mapper.HolidayQueryMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayQueryService {
    private final HolidayQueryMapper holidayQueryMapper;

    public boolean isHoliday(LocalDate date) {
        return holidayQueryMapper.existsByDate(date);
    }

    public List<HolidayQueryDto> getHolidays() {
        return holidayQueryMapper.findAllHolidays();
    }

    public List<HolidayQueryDto> getHolidaysByYear(int year) {
        return holidayQueryMapper.findByYear(year);
    }

    public List<HolidayQueryDto> getHolidaysByMonth(int year, int month) {
        return holidayQueryMapper.findByMonth(year, month);
    }

    public List<HolidayQueryDto> getHolidaysBetween(LocalDate start, LocalDate end) {
        return holidayQueryMapper.findBetweenDates(start, end);
    }

    public Integer countHolidaysBetween(LocalDate start, LocalDate end) {
        return holidayQueryMapper.countBetweenDates(start, end);
    }
}
