package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.query.dto.CompletedProjectDTO;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.ProjectOTD;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.TaskProgressDTO;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import static com.ideality.coreflow.common.exception.ErrorCode.PROJECT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final HolidayQueryService holidayQueryService;
    private final TaskQueryService taskQueryService;

    @Override
    public void existsById(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new BaseException(PROJECT_NOT_FOUND);
        }
    }

    @Override
    public Project createProject(ProjectCreateRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .startBase(request.getStartBase())
                .endBase(request.getEndBase())
                .startExpect(request.getStartBase())
                .endExpect(request.getEndExpect()!=null?request.getEndExpect():request.getEndBase())
                .progressRate(0.0)
                .passedRate(0.0)
                .delayDays(0)
                .status(Status.PENDING)
                .templateId(request.getTemplateId())
                .build();
        projectRepository.save(project);
        return project;
    }

    @Override
    public boolean isCompleted(Long projectId) {
        Project project = projectRepository.findById(projectId).
            orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

		return project.getStatus().equals(Status.COMPLETED);
    }

    @Override
    public Project findById(Long projectId) throws NotFoundException {
        return projectRepository.findById(projectId)
                                            .orElseThrow(()->new NotFoundException("프로젝트가 존재하지 않습니다"));
    }

    @Override
    public Long updateProjectStatus(Project project, Status status) {
        project.setStatus(status);
        if(status.equals(Status.COMPLETED)) {
            project.setEndReal(LocalDate.now());
        }
        projectRepository.save(project);
        return project.getId();
    }

    @Override
    public Double updateProjectPassedRate(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        LocalDate endBase = project.getEndBase();
        LocalDate startBase = project.getStartBase();
        Long totalDuration = ChronoUnit.DAYS.between(startBase, endBase)+1-holidayQueryService.countHolidaysBetween(startBase, endBase);
        log.info("totalDuration = " + totalDuration);

        LocalDate now = LocalDate.now();
        Long passedDates = ChronoUnit.DAYS.between(startBase, now)+1-holidayQueryService.countHolidaysBetween(startBase, now);
        log.info("passedDates = " + passedDates);

        Double passedRate =(double) passedDates/totalDuration*100;
        passedRate = passedRate>100?100:Math.round(passedRate*100)/100.0;
        project.setPassedRate(passedRate);
        projectRepository.saveAndFlush(project);
        return passedRate;
    }

    @Override
    public Double updateProjectProgress(Long projectId, List<TaskProgressDTO> taskList) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        System.out.println("project = " + project);
        Long totalDuration = 0L;
        Double totalProgress = 0.0;
        for (TaskProgressDTO task : taskList) {
            System.out.println("task = " + task);
            Long duration = (ChronoUnit.DAYS.between(task.getStartDate(), task.getEndDate()) + 1
                    - holidayQueryService.countHolidaysBetween(task.getStartDate(), task.getEndDate()));
            totalDuration += duration;
            System.out.println("duration = " + duration);

            Double progress = duration * (task.getProgressRate()/100);
            System.out.println("progress = " + progress);
            totalProgress += progress;
        }
        System.out.println("Num to Save = " + Math.round(totalProgress/totalDuration*10000)/100.0);
        project.setProgressRate(Math.round(totalProgress/totalDuration*10000)/100.0);
        projectRepository.saveAndFlush(project);
        return project.getProgressRate();
    }

    // 프로젝트별 납기준수율 계산하기
    @Override
    public List<ProjectOTD> calculateProjectOTD(List<CompletedProjectDTO> completedProjectList) {

        List<ProjectOTD> OTDList = new ArrayList<>();
        for(CompletedProjectDTO project : completedProjectList) {

            int CompletedOnTime = 0;     // 기한 내 완료 태스크 개수
            int NotCompletedOnTime = 0; // 기한 내 미완료 태스크 개수
            // 특정 프로젝트의 완료된 태스크 목록 가져오기
            List<CompletedTaskDTO> taskList = taskQueryService.selectCompletedTasks(project.getId());
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

}