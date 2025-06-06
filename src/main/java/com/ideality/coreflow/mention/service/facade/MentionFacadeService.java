package com.ideality.coreflow.mention.service.facade;

import com.ideality.coreflow.mention.service.MentionService;
import com.ideality.coreflow.project.command.application.service.ProjectService;
import com.ideality.coreflow.project.query.service.DeptQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentionFacadeService {

    private final ProjectService projectService;
    private final MentionService mentionService;
    private final UserQueryService userQueryService;

    public List<String> getMentionList(Long projectId, String mentionTarget) {

        /* 설명. @하고 입력값 없으면 -> 프로젝트 참여자들 조회 -> 값 있으면 파싱해서 값 조회
         *   부서명 조회 + 회원 조회 구조
        * */
        projectService.existsById(projectId);
        List<String> mentionParse = mentionService.parseTarget(mentionTarget);

        List<String> mentionResult;

        if (mentionParse == null) {
            mentionResult = userQueryService.selectMentionUserByProjectId(projectId);
        } else if (mentionParse.size() == 1) {
            mentionResult = userQueryService.selectMentionUserByDeptName(mentionParse, projectId);
        } else if (mentionParse.size() == 2) {
            mentionResult = userQueryService.selectMentionUserByDeptAndJob(mentionParse, projectId);
        } else {
            mentionResult = userQueryService.selectMentionUserByMentionInfo(mentionParse, projectId);
        }

        return mentionResult;
    }
}
