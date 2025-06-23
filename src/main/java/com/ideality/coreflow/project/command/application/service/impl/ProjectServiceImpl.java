package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.DateInfoDTO;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;

import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.service.TaskQueryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                                            .orElseThrow(()->new BaseException(PROJECT_NOT_FOUND));
    }

    @Override
    public Long updateProjectStatus(Long projectId, Status targetStatus) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        if (project.getStatus() == targetStatus){
            throw new BaseException(ErrorCode.EQUAL_STATUS);
        }

        if (targetStatus == Status.PENDING &&
                !EnumSet.of(Status.DELETED, Status.CANCELLED).contains(project.getStatus())) {
            throw new BaseException(ErrorCode.IMPOSSIBLE_CHANGE_PENDING);
        }

        if (targetStatus == Status.COMPLETED) {
            if (project.getStatus() != Status.PROGRESS) {
                throw new BaseException(ErrorCode.NOT_PROGRESS_STATUS);
            } else {
                project.updateEndReal(LocalDate.now());
            }
        }
        project.updateStatus(targetStatus);

        projectRepository.save(project);
        return project.getId();
    }

    @Override
    public Double updateProjectPassedRate(Long projectId, Double passedRate) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        project.updatePassedRate(passedRate);
        projectRepository.saveAndFlush(project);
        return project.getPassedRate();
    }

    @Override
    public Double updateProjectProgress(Long projectId, Double progress) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        System.out.println("project = " + project);

        log.info("progress = " + progress);
        project.updateProgressRate(progress);
        projectRepository.saveAndFlush(project);
        return project.getProgressRate();
    }

    @Override
    public ProjectDateDTO findProjectDateById(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        return ProjectDateDTO.builder()
                .projectStartBase(project.getStartBase())
                .projectEndBase(project.getEndBase())
                .build();
    }

    @Override
    public void projectSave(Project project) {
        projectRepository.save(project);
    }

    @Override
    public DateInfoDTO findDateInfoByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        return DateInfoDTO.builder()
                .startBase(project.getStartBase())
                .endBase(project.getEndBase())
                .startReal(project.getStartReal())
                .endReal(project.getEndReal())
                .startExpect(project.getStartExpect())
                .endExpect(project.getEndExpect())
                .build();
    }
}