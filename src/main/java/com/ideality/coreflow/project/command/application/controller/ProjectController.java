package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestDeleteParticipant;
import com.ideality.coreflow.project.command.application.dto.RequestInviteUserDTO;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.Project;
import com.ideality.coreflow.project.command.application.dto.ProjectCreateRequest;
import com.ideality.coreflow.project.command.domain.aggregate.Status;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        log.info("역할: {}", roles);
        if (!roles.contains("CREATOR")) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
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
    // 프로젝트 팀장 초대
    @PostMapping("/{projectId}/participants/team-leader")
    public ResponseEntity<APIResponse<?>>
    createTeamLeader(@PathVariable Long projectId,
                     @RequestBody List<RequestInviteUserDTO> reqLeaderDTO) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        projectFacadeService.createParticipantsLeader(userId, projectId, reqLeaderDTO);
        return ResponseEntity.ok(APIResponse.success(null, "팀 리더 초대에 성공하였습니다."));
    }
    // 프로젝트 팀원 초대
    @PostMapping("/{projectId}/participants/team-member")
    public ResponseEntity<APIResponse<?>> createTeamMember
            (@PathVariable Long projectId,
             @RequestBody List<RequestInviteUserDTO> reqMemberDTO) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        projectFacadeService.createParticipantsTeamMember(userId, projectId, reqMemberDTO);
        return ResponseEntity.ok(APIResponse.success(null, "팀원 초대에 성공하였습니다."));
    }

    @DeleteMapping("/participants/delete")
    public ResponseEntity<APIResponse<?>> deleteParticipants(@RequestBody RequestDeleteParticipant request) {

        Long requesterId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        projectFacadeService.deleteParticipants(request, requesterId);
        return ResponseEntity.ok(APIResponse.success(null, "참여자 삭제에 성공하였습니다."));
    }


    // TODO. 프로젝트 분석 리포트 생성
    @GetMapping("/report/{projectId}")
    public void downloadReport(HttpServletResponse response, @PathVariable Long projectId) {
        projectFacadeService.downloadReport(projectId, response);
   }

   // 슬랙타임 업데이트
    @PatchMapping("/{projectId}/update/slackTime")
    public ResponseEntity<APIResponse<?>> updateSlackTime(@PathVariable Long projectId) {
        projectFacadeService.updateSlackTime(projectId);
        return ResponseEntity.ok(APIResponse.success(null, "슬랙타임 업데이트 완료"));
    }
}