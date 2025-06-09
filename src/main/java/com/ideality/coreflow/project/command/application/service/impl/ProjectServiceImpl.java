package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.repository.ProjectRepository;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import static com.ideality.coreflow.common.exception.ErrorCode.PROJECT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

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
    public Long completeProject(Project project) {
        project.setStatus(Status.COMPLETED);
        project.setEndReal(LocalDate.now());
        projectRepository.save(project);
        return project.getId();
    }

    @Override
    public Project findById(Long projectId) throws NotFoundException {
        return projectRepository.findById(projectId)
                                            .orElseThrow(()->new NotFoundException("프로젝트가 존재하지 않습니다"));
    }
}