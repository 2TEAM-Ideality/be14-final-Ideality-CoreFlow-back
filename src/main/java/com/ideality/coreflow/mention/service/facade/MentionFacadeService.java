package com.ideality.coreflow.mention.service.facade;

import com.ideality.coreflow.mention.dto.ResponseMentionDTO;
import com.ideality.coreflow.mention.service.MentionService;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public List<ResponseMentionDTO> getMentionList(Long projectId, String mentionTarget) {
        projectService.existsById(projectId);

        List<String> mentionParse = mentionService.parseTarget(mentionTarget);

        // 유저 검색
        List<String> mentionUsers = userQueryService.selectMentionUserByMentionInfo(mentionParse, projectId);
        List<ResponseMentionDTO> result = mentionUsers.stream()
                .map(name -> new ResponseMentionDTO(name, "USER"))
                .collect(Collectors.toList());

        // 팀 이름만 입력했을 가능성이 있는 경우
        if (mentionParse != null &&mentionParse.size() == 1 && !mentionTarget.contains("_")) {
            List<String> teams = userQueryService.selectTeamByMentionInfo(mentionParse, projectId);
            List<ResponseMentionDTO> teamDtos = teams.stream()
                    .map(team -> new ResponseMentionDTO(team, "TEAM"))
                    .collect(Collectors.toList());
            result.addAll(teamDtos);
        }

        return result;
    }

    public List<ResponseMentionDTO> getDetailList(Long projectId, Long taskId, String detailTarget) {
        projectService.existsById(projectId);
        taskService.validateTask(taskId);

        List<String> detailList = detailQueryService.getDetailList(projectId, taskId, detailTarget);

        List<ResponseMentionDTO> result = detailList.stream()
                .map(name -> new ResponseMentionDTO(name, "DETAIL"))
                .collect(Collectors.toList());
        return result;
    }
}
