package com.ideality.coreflow.comment.command.application.service.facade;

import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentFacadeService {
    private final CommentService commentService;
    private final ParticipantService participantService;
    private final AttachmentCommandService attachmentCommandService;
    private final TaskService taskService;

    @Transactional
    public Long createComment(RequestCommentDTO commentDTO, Long taskId) {
        taskService.validateTask(taskId);

        Long commentId = commentService.createComment(commentDTO, taskId);
        return commentId;
    }
}
