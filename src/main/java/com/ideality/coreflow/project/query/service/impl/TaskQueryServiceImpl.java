package com.ideality.coreflow.project.query.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.tenant.config.TenantContext;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.notification.command.domain.aggregate.NotificationTargetType;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.mapper.ParticipantMapper;
import com.ideality.coreflow.project.query.mapper.RelationMapper;
import com.ideality.coreflow.project.query.mapper.TaskMapper;
import com.ideality.coreflow.project.query.mapper.WorkMapper;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.template.query.dto.EdgeDTO;

import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskQueryServiceImpl implements TaskQueryService {
    private final TaskMapper taskMapper;
    private final RelationMapper relationMapper;
    private final WorkMapper workMapper;
    private final ParticipantMapper participantMapper;
    private final NotificationService notificationService;

    /* 설명. 태스크 상세 정보 가져오는 DTO */
    @Override
    public ResponseTaskInfoDTO selectTaskInfo(Long taskId) {
        SelectTaskDTO dto = taskMapper.selectTaskInfo(taskId);
        ResponseTaskInfoDTO resDto = new ResponseTaskInfoDTO();
        resDto.setSelectTask(dto);
        return resDto;
    }

    @Override
    public List<ResponseTaskDTO> selectTasks(Long projectId) {
        return taskMapper.selectTasks(projectId);
    }

    // 완료된 프로젝트를 위한 태스크 정보 가져오기
    @Override
    public List<CompletedTaskDTO> selectCompletedTasks(Long projectId) {
        return taskMapper.selectCompletedTasks(projectId);
    }

    // 프로젝트별 납기준수율 계산하기
    @Override
    public List<ProjectOTD> calculateProjectDTO(List<CompletedProjectDTO> completedProjectList) {

        List<ProjectOTD> OTDList = new ArrayList<>();
        for(CompletedProjectDTO project : completedProjectList) {

            int CompletedOnTime = 0;     // 기한 내 완료 태스크 개수
            int NotCompletedOnTime = 0; // 기한 내 미완료 태스크 개수
            // 특정 프로젝트의 완료된 태스크 목록 가져오기
            List<CompletedTaskDTO> taskList = taskMapper.selectCompletedTasks(project.getId());
            int taskLength = taskList.size();

            for(CompletedTaskDTO task : taskList) {
                if(task.getDelayDays() > 0){
                    NotCompletedOnTime ++;
                }else{
                    CompletedOnTime ++;
                }
            }
            System.out.println("프로젝트명 -----------" + project.getName());
            System.out.println("전체 태스크 개수"  + taskLength);
            System.out.println("CompletedOnTime = " + CompletedOnTime);
            System.out.println("NotCompletedOnTime = " + NotCompletedOnTime);

            double OTD = taskLength > 0 ? (CompletedOnTime * 100.0) / taskLength : 0.0;
            System.out.println("OTD = " + OTD);
            ProjectOTD newProjectOTD = ProjectOTD.builder()
                    .projectId(project.getId())
                    .projectName(project.getName())
                    .otdRate(OTD)
                    .totalTask(taskLength)
                    .completedOnTime(CompletedOnTime)
                    .notCompletedOnTime(NotCompletedOnTime)
                    .build();
            OTDList.add(newProjectOTD);
        }

        return OTDList;
    }

    @Override
    public List<EdgeDTO> getEdgeList(List<ResponseTaskDTO> taskList) {
        // 1. 태스크 ID 추출
        List<Long> taskIds = taskList.stream()
            .map(ResponseTaskDTO::getId)
            .toList();

        // 2. relation 테이블에서 관련된 모든 관계 조회
        List<RelationDTO> relations = relationMapper.findAllRelationsForTaskIds(taskIds);

        // 3. EdgeDTO로 변환
        return relations.stream()
            .filter(rel -> taskIds.contains(rel.getPrevWorkId()) && taskIds.contains(rel.getNextWorkId()))
            .map(rel -> EdgeDTO.builder()
                .id("e" + rel.getPrevWorkId() + "-" + rel.getNextWorkId())
                .source(String.valueOf(rel.getPrevWorkId()))
                .target(String.valueOf(rel.getNextWorkId()))
                .type("default")
                .build())
            .toList();
    }

    @Override
    public Long getProjectId(Long taskId) {
        return taskMapper.selectProjectIdByTaskId(taskId);
    }

    @Override
    public boolean isAllTaskCompleted(Long projectId) {
        return taskMapper.countIncompleteTasks(projectId)==0;
    }

    @Override
    public Long selectProjectIdByTaskId(Long taskId) {
        Long projectId = taskMapper.selectProjectIdByTaskId(taskId);

        if (projectId == null) {
            throw new BaseException(ErrorCode.TASK_NOT_FOUND);
        }
        return projectId;
    }

    @Override
    public String getTaskName(Long taskId) {
        return taskMapper.selectTaskNameByTaskId(taskId);
    }

    @Override
    public List<WorkProgressDTO> getTaskProgressByProjectId(Long projectId) {
        return taskMapper.selectTaskProgressByProjectId(projectId);
    }

    // 매일 자정에 실행되도록 설정
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Override
    public void sendTaskDueReminder() {
        log.info("알림 작업 시작 - 오늘 마감일인 태스크 확인");

        // 1. 기존 테넌트 정보 가져오기
        String tenant = TenantContext.getTenant();  // 현재 테넌트 정보를 가져옵니다.
        // 2. 여기서 새로운 테넌트를 설정
        TenantContext.setTenant("company_a");  // 예시로 "company_a"로 테넌트 설정

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // 오늘 마감일인 진행 중인 작업 조회
        List<Work> tasksToday = workMapper.findTasksDueToday(today);

        // 내일 마감일인 진행 중인 작업 조회
        List<Work> tasksTomorrow = workMapper.findTasksDueTomorrow(tomorrow);

        // 오늘 마감일인 작업들에 대해 알림 전송
        for (Work task : tasksToday) {
            String taskName = taskMapper.selectTaskNameByTaskId(task.getId());

            // 해당 태스크에 참여한 사용자들을 조회
            List<Long> participants = participantMapper.findParticipantsByTaskId(task.getId());

            // 각 참여자에게 알림 전송
            for (Long userId : participants) {
                notificationService.sendNotification(userId,
                        "오늘은 태스크 ["+taskName+"]의 예상 마감일입니다!",
                        task.getId(),
                        NotificationTargetType.WORK);
            }
        }

        // 내일 마감일인 작업들에 대해 알림 전송
        for (Work task : tasksTomorrow) {
            String taskName = taskMapper.selectTaskNameByTaskId(task.getId());

            // 해당 태스크에 참여한 사용자들을 조회
            List<Long> participants = participantMapper.findParticipantsByTaskId(task.getId());

            // 각 참여자에게 알림 전송
            for (Long userId : participants) {
                notificationService.sendNotification(userId,
                        "내일은 태스크 ["+taskName+"]의 예상 마감일입니다!",
                        task.getId(),
                        NotificationTargetType.WORK);
            }
        }
    }

    @Override
    public List<GanttTaskResponse> getGanttTasksByProjectId(Long projectId) {
        List<GanttTaskResponse> rootTasks = taskMapper.selectRootTasksByProjectId(projectId);

        System.out.println("rootTasks = " + rootTasks);
        
        return rootTasks.stream()
                .map(task -> {
                    List<GanttTaskResponse> children = taskMapper.selectSubTasksByParentId(task.getTaskId());

                    return task.toBuilder()
                            .subTasks(children)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean checkTaskWarning(Long taskId) {
        CheckTaskWarningDTO endExpect = taskMapper.findTaskEndExpect(taskId);       // (1)자기 자신의 예상 종료일, (2)세부일정중 예상일이 가장 늦은 예상 종료일을 가져옴
        System.out.println("endExpect = " + endExpect);                             // log 출력용
        // 태스크의 예상 종료일이 세부일정의 예상 종료일보다 빠르면 warning
        // 태스크의 예상 종료일이 세부일정의 예상 종료일과 같거나 이후라면 warning false
        if(endExpect.getSubTaskEndExpect()==null){
            return false;
        }
        return endExpect.getTaskEndExpect().isBefore(endExpect.getSubTaskEndExpect());
    }

    @Override
    public List<WorkDueTodayDTO> getWorksDueToday(List<Long> projectIds) {
        if (projectIds.isEmpty()) return List.of();
        return taskMapper.findWorksDueToday(projectIds, LocalDate.now());
    }

    @Override
    public Long getNearDueSubtaskCount(Long taskId) {
        return taskMapper.getNearDueSubtaskCount(taskId, LocalDate.now());
    }
}
