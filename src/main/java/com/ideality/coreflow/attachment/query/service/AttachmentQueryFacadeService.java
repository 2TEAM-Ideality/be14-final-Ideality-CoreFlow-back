package com.ideality.coreflow.attachment.query.service;

import com.ideality.coreflow.attachment.query.dto.ResponseCommentAttachmentDTO;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentQueryFacadeService {
    private final AttachmentQueryService attachmentQueryService;
    private final CommentQueryService commentQueryService;
    private final TaskQueryService taskQueryService;
    private final ParticipantQueryService participantQueryService;

    public List<ResponseCommentAttachmentDTO> getAttachmentListByComment(Long taskId, Long userId) {

        /* 설명. 일단 프로젝트 참여자인지 확인 */
        Long projectId = taskQueryService.getProjectId(taskId);

        /* 설명. 잘못된 접근에 대한 예외처리 */
        if (projectId == null) {
            throw new BaseException(TASK_NOT_FOUND);
        }

        /* 설명. 프로젝트 참여자 -> 댓글을 작성할 수 있는 권한이 있는가 */
        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.NOT_COMMENT_WRITER);
        }

        /* 설명. 하나의 태스크 안에 있는 모든 댓글별 첨부파일 조회 */
        List<Long> commentIds = commentQueryService.selectAllCommentsByAttachment(taskId);

        return attachmentQueryService.getAttachmentsByCommentId(commentIds);
    }
}
