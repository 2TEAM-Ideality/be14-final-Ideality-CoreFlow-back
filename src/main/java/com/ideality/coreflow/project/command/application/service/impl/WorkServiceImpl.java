package com.ideality.coreflow.project.command.application.service.impl;

import static com.ideality.coreflow.common.exception.ErrorCode.WORK_NOT_FOUND;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.command.application.service.WorkService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ideality.coreflow.project.query.dto.TaskPreviewDTO;
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
    public Double updatePassedRate(Long workId, double passedRate) {
        Work work = workRepository.findById(workId).orElseThrow(()->new BaseException(WORK_NOT_FOUND));
        work.updatePassedRate(passedRate);
        workRepository.saveAndFlush(work);
        return work.getPassedRate();
    }

    @Override
    public Map<Long, List<TaskPreviewDTO>> findByProjectIdIn(List<Long> projectIds) {
        List<Work> tasks = workRepository.findByProjectIdIn(projectIds);
        log.info("태스크 목록:{}",tasks.toString());

        return tasks.stream()
                .filter(task -> task.getParentTaskId() == null)
                .collect(Collectors.groupingBy(
                        Work::getProjectId,
                        Collectors.mapping(
                                t -> new TaskPreviewDTO(t.getId(), t.getName()),
                                Collectors.toList()
                        )
                ));
    }

    @Override
    public long findProjectIdByTaskId(Long taskId) {
        return workRepository.findById(taskId).orElseThrow(()->new BaseException(WORK_NOT_FOUND)).getProjectId();
    }

    @Override
    public DateInfoDTO findDateInfoByWorkId(Long workId) {
        Work work = workRepository.findById(workId).orElseThrow(()->new BaseException(WORK_NOT_FOUND));
        return DateInfoDTO.builder()
                .startBase(work.getStartBase())
                .endBase(work.getEndBase())
                .startReal(work.getStartReal())
                .endReal(work.getEndReal())
                .startExpect(work.getStartExpect())
                .endExpect(work.getEndExpect())
                .build();
    }

    @Override
    public List<Work> findAllByStatusNotIn(List<Status> statuses) {
        return workRepository.findAllByStatusNotIn(statuses);
    }

    @Override
    public Long findTaskIdByDetailId(Long workId) {
        return workRepository.findById(workId).orElseThrow(() -> new BaseException(WORK_NOT_FOUND)).getParentTaskId();
    }
}
