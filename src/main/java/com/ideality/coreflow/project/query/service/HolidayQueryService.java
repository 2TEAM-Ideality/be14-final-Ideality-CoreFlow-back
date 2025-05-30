package com.ideality.coreflow.project.query.service;

import com.ideality.coreflow.project.query.dto.HolidayQueryDto;
import com.ideality.coreflow.project.query.mapper.HolidayQueryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HolidayQueryService {
    private final HolidayQueryMapper holidayQueryMapper;

    public List<HolidayQueryDto> getHolidaysByYear(int year) {
        return holidayQueryMapper.findByYear(year);
    }
}
