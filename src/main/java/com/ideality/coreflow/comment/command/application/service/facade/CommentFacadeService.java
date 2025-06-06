package com.ideality.coreflow.comment.command.application.service.facade;

import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.application.dto.CreateAttachmentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.infra.s3.UploadFileResult;
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
    private final S3Service s3Service;

    @Transactional
    public Long createComment(RequestCommentDTO commentDTO, Long taskId) {

        /* 설명. 댓글 작성 순서 -> 댓글 작성, 첨부 파일 업로드, 알림에 추가 */
        taskService.validateTask(taskId);

        Long commentId = commentService.createComment(commentDTO, taskId);

        /* 설명. 첨부 파일이 있을 때만 로직을 수행하게끔 흐름 조정 */
        if (commentDTO.getAttachmentFile() != null) {
            UploadFileResult uploadResult =
                    s3Service.uploadFile(commentDTO.getAttachmentFile(), "comment-docs");

            CreateAttachmentDTO attachmentDTO = new CreateAttachmentDTO(uploadResult);
            attachmentCommandService.createAttachmentForComment(attachmentDTO,
                    taskId,
                    commentDTO.getUserId());
        }
        return commentId;
    }
}
