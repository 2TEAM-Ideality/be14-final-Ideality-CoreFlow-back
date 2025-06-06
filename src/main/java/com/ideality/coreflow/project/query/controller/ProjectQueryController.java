package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.service.ProjectQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectQueryController {

    private final ProjectQueryService projectQueryService;

    @GetMapping("/list") // 임시로 userId를 param으로 받아옴. 추후 반드시 수정
    public APIResponse<List<ProjectSummaryDTO>> getProjects(@RequestParam Long userId) {
        List<ProjectSummaryDTO> projects = projectQueryService.selectProjectSummaries(userId);
        int count=projects.size();
        return APIResponse.success(projects, "참여중인 프로젝트 목록 조회 완료 ("+count+"개)");
    }
}
