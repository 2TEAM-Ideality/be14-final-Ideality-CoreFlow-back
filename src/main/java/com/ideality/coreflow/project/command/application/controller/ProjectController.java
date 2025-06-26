package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@RestController
public class ProjectController {

    private final ProjectFacadeService projectFacadeService;

    @PostMapping
    public ResponseEntity<APIResponse<?>> createProject(@RequestBody ProjectCreateRequest request) {
        log.info("Create project request: {}", request.toString());
        Project result = projectFacadeService.createProject(request);
        return ResponseEntity.ok(APIResponse.success(result, result.getId()+"번 프로젝트 생성 완료"));
    }

    @PatchMapping("/{projectId}/status/{status}")
    public ResponseEntity<APIResponse<?>> updateProjectStatus(@PathVariable Long projectId,
                                                                   @PathVariable String status) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        Status targetStatus;
        try {
            targetStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 상태값입니다: " + status);
        }

        Long updatedProjectId = projectFacadeService.updateProjectStatus(projectId, userId, targetStatus);

        return ResponseEntity.ok(APIResponse.success(updatedProjectId, updatedProjectId + "번 프로젝트 상태가 '" + targetStatus + "'로 변경되었습니다"));
    }

    @PatchMapping("/passed-rate")
    public ResponseEntity<APIResponse<?>> updateProjectPassedRate(){
        projectFacadeService.updateAllPassedRates();
        return ResponseEntity.ok(
                APIResponse.success(null, "경과율이 업데이트 되었습니다")
        );
    }

    @PostMapping("/{projectId}/participants/team-leader")
    public ResponseEntity<APIResponse<?>>
    createTeamLeader(@PathVariable Long projectId,
                     @RequestBody List<RequestInviteUserDTO> reqLeaderDTO) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        projectFacadeService.createParticipantsLeader(userId, projectId, reqLeaderDTO);
        return ResponseEntity.ok(APIResponse.success(null, "팀 리더 초대에 성공하였습니다."));
    }

    @PostMapping("/{projectId}/participants/team-member")
    public ResponseEntity<APIResponse<?>> createTeamMember
            (@PathVariable Long projectId,
             @RequestBody List<RequestInviteUserDTO> reqMemberDTO) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        projectFacadeService.createParticipantsTeamLeader(userId, projectId, reqMemberDTO);
        return ResponseEntity.ok(APIResponse.success(null, "팀원 초대에 성공하였습니다."));
    }


    // TODO. 프로젝트 분석 리포트 생성
    @GetMapping("/report/{projectId}")
    public void downloadReport(HttpServletResponse response, @PathVariable Long projectId) {
        projectFacadeService.downloadReport(projectId, response);
   }


}