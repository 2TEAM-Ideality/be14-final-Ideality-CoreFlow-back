package com.ideality.coreflow.comment.command.application.service.facade;

import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.application.dto.CreateAttachmentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestModifyCommentDTO;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.comment.command.domain.aggregate.Comment;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.infra.s3.UploadFileResult;
import com.ideality.coreflow.notification.command.application.service.NotificationRecipientsService;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkQueryService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentFacadeService {
    private final CommentService commentService;
    private final ParticipantQueryService participantQueryService;
    private final AttachmentCommandService attachmentCommandService;
    private final NotificationService notificationService;
    private final NotificationRecipientsService notificationRecipientsService;
    private final WorkQueryService workService;
    private final TaskQueryService taskQueryService;
    private final S3Service s3Service;
    private final UserQueryService userQueryService;

    @Transactional
    public Long createComment(RequestCommentDTO commentDTO, Long taskId, Long userId) {

        /* 설명. 댓글 작성 순서 -> 댓글 작성, 첨부 파일 업로드, 알림에 추가 */
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

        Long commentId = commentService.createComment(commentDTO, taskId, userId);
        Long notificationId = null;
        boolean hasMentions = commentDTO.getMentions() != null && !commentDTO.getMentions().isEmpty();
        boolean hasDetails = commentDTO.getDetails() != null && !commentDTO.getDetails().isEmpty();

        Set<Long> totalRecipients = new HashSet<>();
        if (hasMentions || hasDetails) {
            // 공통 데이터 조회 -> 이름 + 태스크 이름 조회 -> 알림을 위해
            String writerName = userQueryService.getUserName(userId);
            String taskTitle = taskQueryService.getTaskName(taskId);
            // 알림 생성
            String content = String.format("%s TASK에서 '%s'님이 회원님을 언급하였습니다.", taskTitle, writerName);
            notificationId = notificationService.createMentionNotification(taskId, content);
        }

        if (hasMentions) {
            List<String> uniqueMentions = commentDTO.getMentions().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            List<Long> userIdByMention = userQueryService.selectIdByMentionList(uniqueMentions, projectId);
            totalRecipients.addAll(userIdByMention);
        }

        if (hasDetails) {
            List<String> uniqueDetails = commentDTO.getDetails().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            List<Long> detailIdList = workService.selectWorkIdByName(uniqueDetails, taskId);
            log.info("detailIdList: {}", detailIdList);
            for (Long detailId : detailIdList) {
                List<Long> participants = participantQueryService.selectParticipantsList(detailId);
                totalRecipients.addAll(participants);
            }
        }

        if (notificationId != null && !totalRecipients.isEmpty()) {
            totalRecipients.remove(userId);
            notificationRecipientsService.createRecipients(new ArrayList<>(totalRecipients), notificationId);
        }

        /* 설명. 첨부 파일이 있을 때만 로직을 수행하게끔 흐름 조정 */
        if (commentDTO.getAttachmentFile() != null) {
            UploadFileResult uploadResult =
                    s3Service.uploadFile(commentDTO.getAttachmentFile(), "comment-docs");

            CreateAttachmentDTO attachmentDTO = new CreateAttachmentDTO(uploadResult);
            attachmentCommandService.createAttachmentForComment(attachmentDTO,
                    commentId,
                    userId);
        }
        return commentId;
    }

    @Transactional
    public Long deleteComment(Long userId, Long commentId) {;
        Comment comment = commentService.findById(commentId);

        /* 설명. 예외처리 순서 -> 본인이 맞는지, 프로젝트에 혹시 이제 참여를 하는지 */
        if (!comment.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.NOT_COMMENT_WRITER);
        }

        Long projectId = taskQueryService.getProjectId(comment.getWorkId());

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_PROJECT);
        }

        return commentService.updateByDelete(userId, comment);
    }

    @Transactional
    public Long updateComment(RequestModifyCommentDTO reqModify, Long userId, Long commentId) {

        Comment comment = commentService.findById(commentId);


        /* 설명. 예외처리 순서 -> 본인이 맞는지, 프로젝트에 혹시 이제 참여를 하는지 */
        if (!comment.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.NOT_COMMENT_WRITER);
        }

        Long projectId = taskQueryService.getProjectId(comment.getWorkId());

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_PROJECT);
        }

        /* 설명. 첨부 파일이 있을 때만 로직을 수행하게끔 흐름 조정 */
        if (reqModify.getAttachmentFile() != null) {
            UploadFileResult uploadResult =
                    s3Service.uploadFile(reqModify.getAttachmentFile(), "comment-docs");

            CreateAttachmentDTO attachmentDTO = new CreateAttachmentDTO(uploadResult);
            attachmentCommandService.createAttachmentForComment(attachmentDTO,
                    comment.getWorkId(),
                    userId);

            log.info("첨부파일 업로드 완료");
        }
        return commentService.updateComment(reqModify, userId, comment);
    }

    @Transactional
    public Long updateCommentByNotice(Long userId, Long commentId) {
        Comment comment = commentService.findById(commentId);

        /* 설명. 예외처리 순서 -> 본인이 맞는지, 프로젝트에 혹시 이제 참여를 하는지 */

        if (!comment.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.NOT_COMMENT_WRITER);
        }

        Long projectId = taskQueryService.getProjectId(comment.getWorkId());

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);
        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED_PROJECT);
        }

        return commentService.updateByNotice(userId, comment);
    }
}
