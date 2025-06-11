package com.ideality.coreflow.project.command.application.service.impl;

import static com.ideality.coreflow.common.exception.ErrorCode.WORK_NOT_FOUND;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.service.WorkService;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {
    private final WorkRepository workRepository;
    private final HolidayQueryService holidayQueryService;

    @Override
    @Transactional
    public Double updatePassedRate(Long workId) {
        Work work = workRepository.findById(workId).orElseThrow(()->new BaseException(WORK_NOT_FOUND));
        LocalDate now = LocalDate.now();
        LocalDate endBase = work.getEndBase();
        LocalDate startBase = work.getStartBase();
        LocalDate startReal = work.getStartReal();

        Long totalDuration = ChronoUnit.DAYS.between(startBase, endBase)+1
                -holidayQueryService.countHolidaysBetween(startBase, endBase);
        Long passedDates = ChronoUnit.DAYS.between(startReal, now)+1
                -holidayQueryService.countHolidaysBetween(startReal, now);
        Double passedRate = (double) passedDates / totalDuration * 100;
        passedRate = passedRate>100?100:Math.round(passedRate*100)/100.0;
        work.setPassedRate(passedRate);
        workRepository.saveAndFlush(work);
        return work.getPassedRate();
    }
}
