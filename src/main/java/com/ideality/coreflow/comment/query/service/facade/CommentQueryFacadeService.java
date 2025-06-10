package com.ideality.coreflow.comment.query.service.facade;

import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.comment.query.dto.ResponseCommentsDTO;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentQueryFacadeService {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;
    private final TaskQueryService taskQueryService;
    private final ParticipantQueryService participantQueryService;


    public List<ResponseCommentsDTO> selectComments(Long taskId, Long userId) {
        // 태스크에 따른 projectId 가져오기 + 예외 검사 (잘못된 태스크 Id)
        Long projectId = taskQueryService.selectProjectIdByTaskId(taskId);

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        // 검증 끝나면, 해당 댓글 찾아오기
        return commentQueryService.selectComments(taskId);
    }

}
