package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.command.application.dto.EdgeInfoDTO;
import com.ideality.coreflow.project.command.application.service.RelationService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.dto.WorkProgressDTO;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
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
    private final TaskQueryService taskQueryService;
    private final TaskService taskService;
    private final RelationService relationService;

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
        if (totalDuration <= 0) {
            log.warn("Invalid totalDuration ({} days) between {} ~ {}. Returning 0%.", totalDuration, startBase, endBase);
            return 0.0;
        }
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
    public Integer calculateWorkingDuration(LocalDate startReal, LocalDate endReal) {
        if (startReal == null || endReal == null) return null;
        long totalDuration = ChronoUnit.DAYS.between(startReal, endReal) + 1;
        int holidays = holidayQueryService.countHolidaysBetween(startReal, endReal);
        return (int)(totalDuration - holidays);
    }

    // task warning 처리: 태스크 예상 마감일과 하위 세부일정 예상 마감일을 비교 후 비교 결과 저장
    public String updateTaskWarning(Long taskId) {
        Boolean warning = taskQueryService.checkTaskWarning(taskId);    // task 예상 마감일과 하위 세부일정 예상 마감일 비교
        log.info("warning = " + warning);
        taskService.setTaskWarning(taskId, warning);                    // task warning 상태 저장
        return warning?"warning 설정됨":"warning 해제됨";
    }

    @Override
    public Integer calculateSlackTime(Long taskId) {
        Work task = taskService.findById(taskId);

        // 후행 일정을 찾기 위한 정보
        List<EdgeInfoDTO> edgeInfo = relationService.findTargetBySourceId(taskId);
        LocalDate end = null;

        switch(task.getStatus()) {
            case PENDING, PROGRESS -> end = task.getEndExpect();
            case COMPLETED, CANCELLED, DELETED -> end = null;
        }

        if (end == null) return null;

        LocalDate start = null;

        // 후행 일정 중 가장 일찍 시작하는 정보
        for (EdgeInfoDTO edge : edgeInfo) {
            Work nextTask = taskService.findById(edge.getTargetId());

            LocalDate taskStart = null;
            switch (nextTask.getStatus()) {
                case COMPLETED, CANCELLED, DELETED -> taskStart = null;
                case PENDING -> taskStart = nextTask.getStartExpect();
                case PROGRESS -> taskStart = nextTask.getStartReal();
            }
            log.info("후행 taskId = " + edge.getTargetId());
            log.info("taskStart = " + taskStart);
            if (taskStart != null) {
                if (start == null || taskStart.isBefore(start)) {
                    start = taskStart;
                }
            }
        }
        log.info("taskId = " + taskId);
        log.info("end = " + end);
        log.info("start = " + start);

        if (start != null && end.isBefore(start)) {
            long slackTime = ChronoUnit.DAYS.between(end, start) - 1;
            int holidays = holidayQueryService.countHolidaysBetween(end, start);
            log.info("slackTime = " + slackTime);
            log.info("holidays = " + holidays);
            log.info("slackTime - holidays = " + (int) Math.max(slackTime - holidays, 0));
            return (int) Math.max(slackTime - holidays, 0);
        }

        return null;
    }
}
