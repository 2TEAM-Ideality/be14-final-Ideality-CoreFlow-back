package com.ideality.coreflow.project.command.domain.service;

import com.ideality.coreflow.approval.command.application.dto.DelayInfoDTO;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.dto.HolidayQueryDto;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DelayNodeDTO;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.project.query.service.RelationQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.ideality.coreflow.common.exception.ErrorCode.WORK_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class DelayDomainServiceImpl implements DelayDomainService {
    
    private final HolidayQueryService holidayQueryService;
    private final WorkRepository workRepository;
    private final ProjectService projectService;
    private final RelationQueryService relationQueryService;
    private final WorkQueryService workQueryService;

    @PersistenceContext
    private EntityManager em;

    @Override
    public DelayInfoDTO delayAndPropagateLogic(Long taskId, int delayDays, boolean isSimulate) {
        Map<Long, Integer> visited = new HashMap<>();
        Map<Long, Integer> realDelayed = new HashMap<>();
        Queue<DelayNodeDTO> queue = new LinkedList<>();
        
        // 지연된 태스크 개수
        int count = 0;
        // 프로젝트 지연일
        int delayDaysByTask = 0;

        Set<LocalDate> holidays = holidayQueryService.getHolidays().stream()
                .map(HolidayQueryDto::getDate).collect(Collectors.toSet());

        Work startTask = workRepository.findById(taskId).orElseThrow(() -> new BaseException(WORK_NOT_FOUND));
        Project project = projectService.findById(startTask.getProjectId());

        // 영속성 컨텍스트 분리
        if (isSimulate) {
            em.detach(startTask);
            em.detach(project);
        }

        LocalDate projectEndExpect = project.getEndExpect();
        LocalDate originProjectEndExpect = projectEndExpect;
        System.out.println("projectEndExpect = " + projectEndExpect);

        queue.offer(new DelayNodeDTO(taskId, delayDays));
        int startTaskDelay = Math.abs(delayDays - startTask.getSlackTime());

        // 지연 전파
        while (!queue.isEmpty()) {
            DelayNodeDTO currentNode = queue.poll();

            // 현재 taskId에 대해 visited보다 작은 지연일일 경우 스킵
            Integer visitedDelay = visited.get(currentNode.getTaskId());
            if (visitedDelay != null && visitedDelay > currentNode.getDelayDays()) {
                continue;
            }

            // 현재 태스크 지연 설정
            Work currentTask = workRepository.findById(currentNode.getTaskId()).orElseThrow(() -> new BaseException(WORK_NOT_FOUND));
            if (isSimulate) {
                em.detach(currentTask);
            }

            // 현재 태스크의 지연일
            int delayToApply = currentNode.getDelayDays();

            // 현재 태스크의 슬랙타임 및 지연일 설정
            if (currentTask.getSlackTime() >= delayToApply) {
                currentTask.setSlackTime(currentTask.getSlackTime() - delayToApply);
                if (Objects.equals(currentTask.getId(), taskId)) {
                    currentTask.setDelayDays(delayToApply);
                }
                currentTask.setEndExpect(currentTask.getEndExpect().plusDays(
                        calculateDelayExcludingHolidays(currentTask.getEndExpect(), delayDays, holidays)
                ));
            } else {
                log.info("슬랙타임 내에서 해결 실패");
                count++;
                int realDelay = delayToApply - currentTask.getSlackTime();
                realDelayed.put(currentTask.getId(), realDelay);
                System.out.println("delayToApply = " + delayToApply);
                System.out.println("slackTime = " + currentTask.getSlackTime());
                System.out.println("realDelay: " + realDelay);
                System.out.println("taskId = " + currentTask.getId());
                if (Objects.equals(currentTask.getId(), taskId)) {
                    currentTask.setDelayDays(currentTask.getDelayDays() + realDelay);   // 지연일 업데이트는 초기노드만 수정 필요
                }
                currentTask.setSlackTime(0);

                projectEndExpect = delayTaskAndChildren(currentTask, realDelay, holidays, projectEndExpect, isSimulate);

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
                workRepository.save(currentTask);
            }
        }

        // 프로젝트 예상 마감일 업데이트
        if (project.getEndExpect().isBefore(projectEndExpect)) {
            Long projectDelay = ChronoUnit.DAYS.between(project.getEndExpect(), projectEndExpect)
                    -holidayQueryService.countHolidaysBetween(project.getEndExpect(), projectEndExpect);
            // 프로젝트 지연일수 업데이트
            // 해당 지연으로 밀린 프로젝트 지연일
            delayDaysByTask = Math.toIntExact(projectDelay);
            project.setEndExpect(projectEndExpect);
            project.setDelayDays(project.getDelayDays() + delayDaysByTask);


            if (!isSimulate) {
                projectService.projectSave(project);
            }

            // 프로젝트 마감일이 변경되었으면, 프로젝트 디렉터에게 알림 보내기, 시뮬레이션이 아니면
//            Long directorUserId = participantQueryService.findDirectorByProjectId(project.getId());
//            String directorContent = "프로젝트 [" + project.getName() + "]의 예상 마감일이 지연되었습니다!";
//            notificationService.sendNotification(directorUserId, directorContent, project.getId(), NotificationTargetType.PROJECT);
        }

        visited.put(startTask.getId(), startTaskDelay);

        // 반환할 값
        log.info("기존 프로젝트 예상 마감일: {}", originProjectEndExpect);
        log.info("전체 지연일: {}", delayDaysByTask);
        log.info("지연 반영 된 프로젝트 마감일: {}", projectEndExpect);
        log.info("결재 요청 온 현재 태스크 지연일:{}", startTaskDelay);
        log.info("지연된 태스크 갯수:{}", count);
        log.info("영향 받은 태스크 id별 지연일:{}", visited);


        return DelayInfoDTO.builder()
                .originProjectEndExpect(originProjectEndExpect)
                .newProjectEndExpect(projectEndExpect)
                .delayDaysByTask(delayDaysByTask)
                .taskCountByDelay(count)
                .delayDaysByTaskId(realDelayed)
                .build();
    }

    // 지연일 계산
    private int calculateDelayExcludingHolidays(LocalDate startDate, int delayDays, Set<LocalDate> holidays) {
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

    private void adjustWorkPeriod(Work work, int delayDays, Set<LocalDate> holidays) {
        if (work.getStatus() == Status.PENDING) {
            work.setStartExpect(work.getStartExpect().plusDays(
                    calculateDelayExcludingHolidays(work.getStartExpect(), delayDays, holidays)
            ));
        }
        if (work.getStatus() == Status.PENDING || work.getStatus() == Status.PROGRESS) {}
        work.setEndExpect(work.getEndExpect().plusDays(
                calculateDelayExcludingHolidays(work.getEndExpect(), delayDays, holidays)
        ));
    }

    private void delaySingleWork(Work work, int delayDays, Set<LocalDate> holidays, boolean isSimulate) {
        adjustWorkPeriod(work, delayDays, holidays);
        if (isSimulate) em.detach(work);
        else workRepository.save(work);
    }

    private LocalDate delayTaskAndChildren(
            Work task, int delayDays, Set<LocalDate> holidays,
            LocalDate projectEndExpect, boolean isSimulate) {

        // 현재 태스크 지연
        delaySingleWork(task, delayDays, holidays, isSimulate);

        // 자식도 지연
        if(projectEndExpect.isBefore(task.getEndExpect())) {
            projectEndExpect = task.getEndExpect();
        }
        if (!isSimulate) {
            workRepository.save(task);
        }
        // 하위 세부일정들 불러오기
        List<Long> detailIds = workQueryService.selectWorkIdsByParentTaskId(task.getId());

        for (Long detailId : detailIds) {
            Work detailWork = workRepository.findById(detailId).orElseThrow(() -> new BaseException(WORK_NOT_FOUND));
            if (isSimulate) {
                em.detach(detailWork);
            }
            delaySingleWork(detailWork, delayDays, holidays, isSimulate);
        }

        return projectEndExpect;
    }
}
