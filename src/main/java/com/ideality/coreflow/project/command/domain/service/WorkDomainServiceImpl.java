package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.dto.WorkProgressDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
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
    public double calculatePassedRate(DateInfoDTO dateInfo, TargetType type) {
        LocalDate now = LocalDate.now();
        LocalDate endBase = dateInfo.getEndBase();
        LocalDate startBase = dateInfo.getStartBase();
        LocalDate startExpect = dateInfo.getStartExpect();

        long totalDuration = ChronoUnit.DAYS.between(startBase, endBase) + 1
                - holidayQueryService.countHolidaysBetween(startBase, endBase);
        log.info("totalDuration = " + totalDuration);
        long passedDates = 0;
        if (type == TargetType.PROJECT) {
            passedDates = ChronoUnit.DAYS.between(startBase, now);
            passedDates = Math.max(0, passedDates) + 1;
            passedDates -= holidayQueryService.countHolidaysBetween(startBase, now);
        } else {
            passedDates = ChronoUnit.DAYS.between(startExpect, now);
            passedDates = Math.max(0, passedDates) + 1;
            passedDates -= holidayQueryService.countHolidaysBetween(startExpect, now);
        }
        passedDates = Math.max(passedDates, 0); // 최종 음수 방지
        log.info("passedDates = " + passedDates);
        double passedRate = (double) passedDates / totalDuration * 100;
        return passedRate > 100 ? 100 : Math.round(passedRate*100) / 100.0;
    }

    // 두 기간 사이의 워킹데이 계산
    @Override
    public Integer calculateWorkingDutarion(LocalDate startReal, LocalDate endReal) {
        if (startReal == null || endReal == null) return null;
        long totalDuration = ChronoUnit.DAYS.between(startReal, endReal) + 1;
        int holidays = holidayQueryService.countHolidaysBetween(startReal, endReal);
        return (int)(totalDuration - holidays);
    }
}
