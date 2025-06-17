package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
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
import com.ideality.coreflow.project.query.mapper.ParticipantMapper;
import com.ideality.coreflow.notification.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.query.mapper.TaskMapper;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.template.query.dto.NodeDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static com.ideality.coreflow.common.exception.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final HolidayQueryService holidayQueryService;

    private final ParticipantMapper participantMapper;
    private final NotificationService notificationService;
    private final TaskQueryService taskQueryService;

    private final ProjectService projectService;
    private final WorkQueryService workQueryService;
    private final RelationQueryService relationQueryService;
    private final WorkRepository workRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    @PersistenceContext
    private EntityManager em;

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
                .delayDays(0)
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

        // 후행 태스크에 참여하는 사용자에게 알림 전송
        List<Long> userIds = participantMapper.findNextTaskUsersByTaskId(taskId);

        String taskName =  taskQueryService.getTaskName(taskId);

        // 각 사용자에게 알림 전송
        for (Long userId : userIds) {
            notificationService.sendNotification(userId, "선행 태스크["+ taskName + "]가 완료되었습니다.", updatedTask.getId(), TargetType.WORK);
        }

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
        if (source == null) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        for (Long sourceId : source) {
            if (sourceId == null || !taskRepository.existsById(sourceId)) {
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
    public Integer delayAndPropagate(Long taskId, Integer delayDays, boolean isSimulate) {
        Map<Long, Integer> visited = new HashMap<>();   // 지연일
        Queue<DelayNodeDTO> queue = new LinkedList<>();
        Integer count = 0;
        Set<LocalDate> holidays = holidayQueryService.getHolidays().stream()
                .map(HolidayQueryDto::getDate).collect(Collectors.toSet());

        // 초기 태스크 조회

        // 지연된 태스크 ID를 추적할 Set
        Set<Long> delayedTaskIds = new HashSet<>();

        // 초기 태스크 처리
        Work startTask = taskRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
        Project project = projectRepository.findById(startTask.getProjectId()).orElseThrow(()->new BaseException(PROJECT_NOT_FOUND));
        LocalDate projectEndExpect = project.getEndExpect();
        System.out.println("projectEndExpect = " + projectEndExpect);

//        String firstTaskName = taskMapper.selectTaskNameByTaskId(startTask.getId());
//
//        // 첫 번째 태스크가 이미 지연되었으므로 무조건 알림 보내기
//        delayedTaskIds.add(startTask.getId());  // 첫 번째 태스크 ID 무조건 추가
//        String content = "태스크 [" + startTask.getName() + "]가 지연되어 예상마감일이 변경되었습니다!";
//
//        // 첫 번째 태스크에 참여한 인원에게 알림 보내기
//        List<Long> firstparticipants = participantMapper.findParticipantsByTaskId(startTask.getId());
//        if (firstparticipants == null || firstparticipants.isEmpty()) {
//            log.warn("참여자 목록이 비어 있습니다. 태스크 ID: " + startTask.getId());
//        }
//        for (Long userId : firstparticipants) {
//            notificationService.sendNotification(userId, content, startTask.getId(), TargetType.WORK);
//        }

        // 지연 전파
        queue.offer(new DelayNodeDTO(taskId, delayDays));

        while (!queue.isEmpty()) {
            DelayNodeDTO currentNode = queue.poll();

            // 현재 taskId에 대해 visited보다 작은 지연일일 경우 스킵
            Integer visitedDelay = visited.get(currentNode.getTaskId());
            if (visitedDelay != null && visitedDelay > currentNode.getDelayDays()) {
                continue;
            }

            // 현재 태스크 지연 설정
            Work currentTask = taskRepository.findById(currentNode.getTaskId()).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));
            if (isSimulate) {
                em.detach(currentTask);
            }

            // 현재 태스크의 지연일
            int delayToApply = currentNode.getDelayDays();

            // 현재 태스크의 슬랙타임 및 지연일 설정
            if (currentTask.getSlackTime() >= delayToApply) {
                currentTask.setSlackTime(currentTask.getSlackTime() - delayToApply);
                currentTask.setDelayDays(delayToApply);
                currentTask.setEndExpect(currentTask.getEndExpect().plusDays(
                        calculateDelayExcludingHolidays(currentTask.getEndExpect(), delayDays, holidays)
                ));
            } else {
                log.info("슬랙타임 내에서 해결 실패");
                count++;
                int realDelay = delayToApply - currentTask.getSlackTime();
                System.out.println("delayToApply = " + delayToApply);
                System.out.println("slackTime = " + currentTask.getSlackTime());
                System.out.println("realDelay: " + realDelay);
                System.out.println("taskId = " + currentTask.getId());
                currentTask.setDelayDays(currentTask.getDelayDays() + realDelay);
                currentTask.setSlackTime(0);

                // 예상 마감일이 변경될 경우만 추적
                LocalDate originalEndExpect = currentTask.getEndExpect();

                // 현재 태스크와 세부일정 예상 마감일 미루기
                projectEndExpect=delayTask(currentTask, realDelay, holidays, projectEndExpect, isSimulate);

                // 예상 종료일이 변경된 경우에만 태스크ID 추가
                if (!currentTask.getEndExpect().equals(originalEndExpect)) {
                    delayedTaskIds.add(currentTask.getId());  // 실제로 마감일이 변경된 태스크만 추가
                    // 지연된 태스크에 참여한 인원에게 알림 보내기
                    List<Long> participants = participantMapper.findParticipantsByTaskId(currentTask.getId());
                    String contents = "태스크 [" + startTask.getName() + "]가 지연되어 ["+ currentTask.getName() +"]의 예상마감일이 변경되었습니다!";
                    for (Long userId : participants) {
                        notificationService.sendNotification(userId, contents, currentTask.getId(), TargetType.WORK);
                    }
                }

                // 다음 노드에 realDelay를 전파
                List<Long> nextTaskIds = relationQueryService.findNextTaskIds(currentNode.getTaskId());

                for (Long nextTaskId  : nextTaskIds) {
                    // 다음 노드에 저장된 지연일
                    Integer storedDelay = visited.get(nextTaskId);
                    if (storedDelay == null || realDelay > storedDelay) {
                        visited.put(nextTaskId, realDelay);
                        queue.offer(new DelayNodeDTO(nextTaskId, realDelay));
                    }
                }
            }
            if (!isSimulate) {
                taskRepository.save(currentTask);
            }
        }

        // 프로젝트 예상 마감일 업데이트
        if (project.getEndExpect().isBefore(projectEndExpect)) {
            Long projectDelay = ChronoUnit.DAYS.between(project.getEndExpect(), projectEndExpect)
                    -holidayQueryService.countHolidaysBetween(project.getEndExpect(), projectEndExpect);
        // 프로젝트 지연일수 업데이트
            project.setEndExpect(projectEndExpect);
            project.setDelayDays(project.getDelayDays()+Math.toIntExact(projectDelay));
            projectRepository.save(project);

            // 프로젝트 마감일이 변경되었으면, 프로젝트 디렉터에게 알림 보내기
            Long directorUserId = participantMapper.findDirectorByProjectId(project.getId());
            String directorContent = "프로젝트 [" + project.getName() + "]의 예상 마감일이 지연되었습니다!";
            notificationService.sendNotification(directorUserId, directorContent, project.getId(), TargetType.PROJECT);
        }


        // 지연된 태스크 ID 출력
        System.out.println("지연된 태스크 ID들: " + delayedTaskIds);

        return count;
    }

    private LocalDate delayTask(Work task, Integer delayDays, Set<LocalDate> holidays, LocalDate projectEndExpect, boolean isSimulate) {
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
        if (!isSimulate) {
            taskRepository.save(task);
        }

        // 태스크 하위 세부일정들도 지연 처리
        // 하위 세부일정들 불러오기
        List<Long> detailIds = workQueryService.selectWorkIdsByParentTaskId(task.getId());
        // 하위 세부일정 지연 처리
        if (detailIds != null) {
            for (Long detailId : detailIds) {
                Work detailWork = workRepository.findById(detailId).orElseThrow(() -> new BaseException(WORK_NOT_FOUND));
                if (isSimulate) {
                    em.detach(detailWork);
                }
                delayWork(detailWork, delayDays, holidays, isSimulate);
            }
        }
        return projectEndExpect;
    }

    private void delayWork(Work work, Integer delayDays, Set<LocalDate> holidays, boolean isSimulate) {
        if (work.getStatus() == Status.PENDING) {
            work.setStartExpect(work.getStartExpect().plusDays(
                    calculateDelayExcludingHolidays(work.getStartExpect(), delayDays, holidays)
            ));
        }
        if (work.getStatus() == Status.PENDING || work.getStatus() == Status.PROGRESS) {
            work.setEndExpect(work.getEndExpect().plusDays(
                    calculateDelayExcludingHolidays(work.getEndExpect(), delayDays, holidays)
            ));
        }
        if (!isSimulate) {
            workRepository.save(work);
        }
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
