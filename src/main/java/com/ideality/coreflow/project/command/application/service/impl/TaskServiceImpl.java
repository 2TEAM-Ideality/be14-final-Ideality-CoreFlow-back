package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DelayNodeDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.command.domain.repository.RelationRepository;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.template.query.dto.NodeDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static com.ideality.coreflow.common.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final HolidayQueryService holidayQueryService;
    private final ProjectService projectService;
    private final WorkQueryService workQueryService;
    private final RelationQueryService relationQueryService;
    private final WorkQueryService workQueryService;
    private final WorkRepository workRepository;
    private final ProjectRepository projectRepository;


    @Override
    @Transactional
    public Long createTask(RequestTaskDTO taskDTO) {
        Work taskWork = Work.builder()
                .name(taskDTO.getLabel())
                .description(taskDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .startBase(taskDTO.getStartBaseLine())
                .endBase(taskDTO.getEndBaseLine())
                .startExpect(taskDTO.getStartBaseLine())
                .endExpect(taskDTO.getEndBaseLine())
                .status(Status.PENDING)
                .projectId(taskDTO.getProjectId())
                .build();
        taskRepository.saveAndFlush(taskWork);
        log.info("Task created with id {}", taskWork.getId());
        return taskWork.getId();
    }


    @Override
    @Transactional
    public Long updateStatusProgress(Long taskId) {
        Work updatedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        updatedTask.startTask();
        return updatedTask.getId();
    }

    @Override
    @Transactional
    public Long updateStatusComplete(Long taskId) {
        Work updatedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        if (updatedTask.getProgressRate() != 100) {
            throw new BaseException(TASK_PROGRESS_NOT_COMPLETED);
        }

        updatedTask.endTask();
        return updatedTask.getId();
    }

    @Override
    @Transactional
    public Long softDeleteTask(Long taskId) {
        Work deleteTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        deleteTask.softDeleteTask();
        return deleteTask.getId();
    }

    @Override
    public void validateRelation(List<Long> source) {
        if (source.isEmpty() || source.contains(null)) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }
        for (Long sourceId : source) {
            if (!taskRepository.existsById(sourceId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }

    @Override
    public void validateTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new BaseException(TASK_NOT_FOUND);
        }
    }

    @Override
    public Double updateTaskProgress(Long taskId) {
        List<TaskProgressDTO> workList = workQueryService.getDetailProgressByTaskId(taskId);
        Work task = taskRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
        Long totalDuration = 0L;
        Double totalProgress = 0.0;
        for (TaskProgressDTO work : workList) {
            log.info(work.toString());
            Long duration = (ChronoUnit.DAYS.between(work.getStartDate(), work.getEndDate()) + 1
                    - holidayQueryService.countHolidaysBetween(work.getStartDate(), work.getEndDate()));
            totalDuration += duration;
            System.out.println("duration = " + duration);

            Double progress = duration * (work.getProgressRate()/100);
            System.out.println("progress = " + progress);
            totalProgress += progress;
        }
        System.out.println("Num to Save = " + Math.round(totalProgress/totalDuration*10000)/100.0);
        task.setProgressRate(Math.round(totalProgress/totalDuration*10000)/100.0);
        taskRepository.saveAndFlush(task);

        // 프로젝트 진척률도 수정
        projectService.updateProjectProgress(task.getProjectId());

        return task.getProgressRate();
    }

    @Override
    @Transactional
    public Integer delayAndPropagate(Long taskId, Integer delayDays) {
        Set<Long> visited = new HashSet<>();
        Queue<DelayNodeDTO> queue = new LinkedList<>();
        Integer count = 0;
        Set<LocalDate> holidays = holidayQueryService.getHolidays().stream()
                .map(HolidayQueryDto::getDate).collect(Collectors.toSet());

        // 초기 태스크 처리
        Work startTask = taskRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
        Project project = projectRepository.findById(startTask.getProjectId()).orElseThrow(()->new BaseException(PROJECT_NOT_FOUND));
        LocalDate projectEndExpect = project.getEndExpect();
        System.out.println("projectEndExpect = " + projectEndExpect);
        projectEndExpect=delayTask(startTask, delayDays, holidays, projectEndExpect);
        startTask.setDelayDays(delayDays);
        taskRepository.save(startTask);

        // 지연 전파
        queue.offer(new DelayNodeDTO(taskId, delayDays));

        while (!queue.isEmpty()) {
            DelayNodeDTO currentNode = queue.poll();
            List<Long> nextTaskIds = relationQueryService.findNextTaskIds(currentNode.getTaskId());

            for (Long nextTaskId  : nextTaskIds) {
                if (visited.contains(nextTaskId)) {continue;}

                Work nextTask = taskRepository.findById(nextTaskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
                Integer delayToApply = currentNode.getDelayDays();

                if (nextTask.getSlackTime() >= delayToApply) {
                    nextTask.setSlackTime(nextTask.getSlackTime() - delayToApply);
                    taskRepository.save(nextTask);
                }else {
                    int realDelay = delayToApply - nextTask.getSlackTime();
                    nextTask.setSlackTime(0);
                    projectEndExpect = delayTask(nextTask, realDelay, holidays, projectEndExpect);
                    queue.offer(new DelayNodeDTO(nextTaskId, realDelay));
                    count++;
                }
                visited.add(nextTaskId);
            }
        }

        // 프로젝트 예상 마감일 업데이트
        if (project.getEndExpect().isBefore(projectEndExpect)) {
            Long projectDelay = ChronoUnit.DAYS.between(project.getEndExpect(), projectEndExpect);
        // 프로젝트 지연일수 업데이트
            project.setEndExpect(projectEndExpect);
            project.setDelayDays(Math.toIntExact(projectDelay));
            projectRepository.save(project);
        }


        return count;
    }

    private LocalDate delayTask(Work task, Integer delayDays, Set<LocalDate> holidays, LocalDate projectEndExpect) {
        if (task.getStatus() == Status.PENDING) {
            task.setStartExpect(task.getStartExpect().plusDays(
                    calculateDelayExcludingHolidays(task.getStartExpect(), delayDays, holidays)
            ));
        }
        task.setEndExpect(task.getEndExpect().plusDays(
                calculateDelayExcludingHolidays(task.getEndExpect(), delayDays, holidays)
        ));
        System.out.println("task.getId() = " + task.getId());
        System.out.println("task.getEndExpect() = " + task.getEndExpect());
        if(projectEndExpect.isBefore(task.getEndExpect())) {
            projectEndExpect = task.getEndExpect();
        }
        System.out.println("projectEndExpect = " + projectEndExpect);
        taskRepository.save(task);

        // 태스크 하위 세부일정들도 지연 처리
        // 하위 세부일정들 불러오기
        List<Long> detailIds = workQueryService.selectWorkIdsByParentTaskId(task.getId());
        // 하위 세부일정 지연 처리
        for(Long detailId : detailIds) {
            Work detailWork = workRepository.findById(detailId).orElseThrow(() -> new BaseException(WORK_NOT_FOUND));
            delayWork(detailWork, delayDays, holidays);
        }
        return projectEndExpect;
    }

    private void delayWork(Work work, Integer delayDays, Set<LocalDate> holidays) {
        if (work.getStatus() == Status.PENDING) {
            work.setStartExpect(work.getStartExpect().plusDays(
                    calculateDelayExcludingHolidays(work.getStartExpect(), delayDays, holidays)
            ));
        }
        work.setEndExpect(work.getEndExpect().plusDays(
                calculateDelayExcludingHolidays(work.getEndExpect(), delayDays, holidays)
        ));
        workRepository.save(work);
    }

    private int calculateDelayExcludingHolidays(LocalDate startDate, Integer delayDays, Set<LocalDate> holidays) {
        int addedDays = 0;
        int workingDays = 0;
        LocalDate date = startDate;

        while (workingDays < delayDays) {
            date = date.plusDays(1);
            if(!holidays.contains(date)) {
                workingDays++;
            }
            addedDays++;
        }
        return addedDays;
    }
}
