package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.query.dto.WorkProgressDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkDomainServiceImpl implements WorkDomainService {

    private final HolidayQueryService holidayQueryService;

    public double calculateProgressRate(List<WorkProgressDTO> works) {
        long totalDuration = 0L;
        double totalProgress = 0.0;

        for (WorkProgressDTO work : works) {
            long duration = ChronoUnit.DAYS.between(work.getStartDate(), work.getEndDate()) + 1
                    - holidayQueryService.countHolidaysBetween(work.getStartDate(), work.getEndDate());
            totalDuration += duration;
            totalProgress += duration * (work.getProgressRate() / 100);
        }

        if (totalDuration == 0) return 0.0;

        return Math.round(totalProgress / totalDuration * 10000) / 100.0;
    }

    @Override
    public double calculatePassedRate(DateInfoDTO dateInfo) {
        LocalDate now = LocalDate.now();
        LocalDate endBase = dateInfo.getEndBase();
        LocalDate startBase = dateInfo.getStartBase();
        LocalDate startReal = dateInfo.getStartReal();

        long totalDuration = ChronoUnit.DAYS.between(startBase, endBase) + 1
                - holidayQueryService.countHolidaysBetween(startBase, endBase);
        long passedDates = ChronoUnit.DAYS.between(startReal, now) + 1
                - holidayQueryService.countHolidaysBetween(startReal, now);
        Double passedRate = (double) passedDates / totalDuration * 100;
        return passedRate > 100 ? 100 : Math.round(passedRate*100) / 100.0;
    }
}
