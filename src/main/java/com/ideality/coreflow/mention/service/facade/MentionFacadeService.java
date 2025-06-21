package com.ideality.coreflow.mention.service.facade;

import com.ideality.coreflow.mention.dto.ResponseMentionDTO;
import com.ideality.coreflow.mention.service.MentionService;
import com.ideality.coreflow.org.query.service.DeptQueryService;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.query.dto.DetailMentionDTO;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.user.query.dto.UserMentionDTO;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentionFacadeService {

    private final ProjectService projectService;
    private final MentionService mentionService;
    private final UserQueryService userQueryService;
    private final WorkQueryService detailQueryService;
    private final TaskService taskService;
    private final DeptQueryService deptQueryService;

    public List<ResponseMentionDTO> getMentionList(Long projectId, String mentionTarget) {
        projectService.existsById(projectId);

        List<String> mentionParse = mentionService.parseTarget(mentionTarget);
        List<ResponseMentionDTO> result = new ArrayList<>();

        // 1. 유저 검색 (항상 수행)
        List<UserMentionDTO> mentionUsers = userQueryService.selectMentionUserByMentionInfo(mentionParse, projectId);
        result.addAll(mentionUsers.stream()
                .map(dto -> new ResponseMentionDTO(dto.getId(), dto.getDeptName() + "_" + dto.getName(), "USER"))
                .collect(Collectors.toList()));

        // 2. 팀 검색 (항상 수행)
        List<String> teams = userQueryService.selectTeamByMentionInfo(mentionParse, projectId);
        log.info(teams.toString());
        if (!teams.isEmpty()) {
            result.addAll(teams.stream()
                    .map(team -> {
                        Long deptId = deptQueryService.findDeptIdByName(team);
                        return new ResponseMentionDTO(deptId, team, "TEAM");
                    })
                    .collect(Collectors.toList()));
        }

        return result;
    }

    public List<ResponseMentionDTO> getDetailList(Long projectId, Long taskId, String detailTarget) {
        projectService.existsById(projectId);
        taskService.validateTask(taskId);

        List<DetailMentionDTO> detailList = detailQueryService.getDetailList(projectId, taskId, detailTarget);

        List<ResponseMentionDTO> result = detailList.stream()
                .map(dto -> new ResponseMentionDTO(dto.getId(), dto.getName(), "DETAIL"))
                .collect(Collectors.toList());
        return result;
    }
}
