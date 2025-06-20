package com.ideality.coreflow.project.query.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.query.dto.CompletedProjectDTO;
import com.ideality.coreflow.project.query.dto.*;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;
import com.ideality.coreflow.project.query.dto.PipelineResponseDTO;
import com.ideality.coreflow.project.query.dto.ProjectSummaryDTO;
import com.ideality.coreflow.project.query.dto.ResponseInvitableUserDTO;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.facade.ProjectQueryFacadeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.checkerframework.checker.units.qual.C;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectQueryController {

    private final ProjectQueryFacadeService projectQueryFacadeService;
    private final TaskQueryService taskQueryService;

    @GetMapping("/list") // 임시로 userId를 param으로 받아옴. 추후 반드시 수정
    public APIResponse<List<ProjectSummaryDTO>> getProjects() {
        long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<ProjectSummaryDTO> projects = projectQueryFacadeService.selectProjectSummaries(userId);
        int count=projects.size();
        return APIResponse.success(projects, "참여중인 프로젝트 목록 조회 완료 ("+count+"개)");
    }

    @GetMapping("/{projectId}")
    public APIResponse<ProjectDetailResponseDTO> getProject(@PathVariable Long projectId) {
        ProjectDetailResponseDTO project = projectQueryFacadeService.getProjectDetail(projectId);
        return APIResponse.success(project, projectId+"번 프로젝트: "+project.getName()+" 조회 완료");
    }

    @GetMapping("/{projectId}/pipeline")
    public APIResponse<PipelineResponseDTO> getPipeline(@PathVariable Long projectId) {
        PipelineResponseDTO project = projectQueryFacadeService.getPipeline(projectId);
        return APIResponse.success(project, project.getName() + " 파이프라인 조회 성공");
    }

    @GetMapping("/{projectId}/invitable-user")
    public ResponseEntity<APIResponse<List<ResponseInvitableUserDTO>>> getInvitableUser(@PathVariable Long projectId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<ResponseInvitableUserDTO> res = projectQueryFacadeService.getInvitableUser(projectId, userId);

        return ResponseEntity.ok(APIResponse.success(res, "초대 가능한 회원 리스트 조회 성공"));
    }

    @GetMapping("/{projectId}/participants/department")
    public ResponseEntity<APIResponse<List<ParticipantDepartmentDTO>>> getParticipantDepartment(@PathVariable Long projectId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<ParticipantDepartmentDTO> reqDTO = projectQueryFacadeService.getParticipantDepartment(projectId, userId);
        return ResponseEntity.ok(APIResponse.success(reqDTO, "부서 조회 완료"));
    }

    @GetMapping("/{projectId}/participants/department/team-leader")
    public ResponseEntity<APIResponse<List<DepartmentLeaderDTO>>>
    getTeamLeaderByDepartment(@PathVariable Long projectId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<DepartmentLeaderDTO> reqDTO = projectQueryFacadeService.getTeamLeaderByDepartment(projectId, userId);
        return ResponseEntity.ok(APIResponse.success(reqDTO, "부서별 책임자 조회 완료"));
    }

    @GetMapping("/{projectId}/participants/by-department")
    public ResponseEntity<APIResponse<List<ResponseParticipantDTO>>>
    getParticipantsByDepartment(@PathVariable Long projectId, @RequestParam String deptName) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<ResponseParticipantDTO> reqDTO =
                projectQueryFacadeService.getParticipantByDepartment(projectId, userId, deptName);

        return ResponseEntity.ok(APIResponse.success(reqDTO, "부서 별 참여자 조회 완료"));
    }

    @PostMapping("/tasks/list")
    public ResponseEntity<APIResponse<?>> getTasks(@RequestBody RequestProjectIds request) {
        return ResponseEntity.ok(APIResponse.success(projectQueryFacadeService.selectTaskSummaries(request.getProjectIds()),"참여 중인 프로젝트의 태스크 조회"));
    }

    @PostMapping("/participants/list")
    public ResponseEntity<APIResponse<?>> getParticipants(@RequestBody RequestProjectIds request) {
        return ResponseEntity.ok(APIResponse.success(projectQueryFacadeService.selectParticipantSummaries(request.getProjectIds()), "참여자 조회"));
    }

    // 완료된 프로젝트 목록 조회
    @GetMapping("/completed")
    public ResponseEntity<APIResponse<List<CompletedProjectDTO>>> getCompletedProjects() {
        List<CompletedProjectDTO> completedProjects = projectQueryFacadeService.getCompletedProjectList();
        return ResponseEntity.ok(APIResponse.success(completedProjects, "완료된 프로젝트 리스트 조회 성공"));
    }

    @GetMapping("/{projectId}/gantt")
    public ResponseEntity<APIResponse<?>> getGanttTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(
                APIResponse.success(projectQueryFacadeService.getGanttTasksByProjectId(projectId),
                        projectId + "번 프로젝트 간트차트 조회 완료")
        );
    }

    // 부서 참여 프로젝트 목록 조회
    @PostMapping("/dept")
    public ResponseEntity<APIResponse<?>> getProjectsByDept(@RequestBody RequestDeptDTO deptName) {
        List<ProjectSummaryDTO> projectList = projectQueryFacadeService.getProjectsByDeptId(deptName);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("deptInfo", deptName);
        responseData.put("projectList", projectList);

        return ResponseEntity.ok(APIResponse.success(responseData, "부서별 참여 프로젝트 목록 조회 성공"));

    }

    @GetMapping("/{projectId}/gantt/project-date")
    public ResponseEntity<APIResponse<?>> getGanttProjectDate(@PathVariable Long projectId) {
        return ResponseEntity.ok(APIResponse.success(projectQueryFacadeService.getGanttProjectDate(projectId), "프로젝트 기간 조회"));
    }
}
