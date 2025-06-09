package com.ideality.coreflow.comment.command.application.service.facade;

import com.ideality.coreflow.attachment.command.application.service.AttachmentCommandService;
import com.ideality.coreflow.attachment.command.application.dto.CreateAttachmentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestModifyCommentDTO;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.infra.s3.S3Service;
import com.ideality.coreflow.infra.s3.UploadFileResult;
import com.ideality.coreflow.mention.service.MentionService;
import com.ideality.coreflow.notification.command.application.service.NotificationRecipientsService;
import com.ideality.coreflow.notification.command.application.service.NotificationService;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.query.service.ParticipantQueryService;
import com.ideality.coreflow.project.query.service.TaskQueryService;
import com.ideality.coreflow.project.query.service.WorkService;
import com.ideality.coreflow.user.query.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ideality.coreflow.common.exception.ErrorCode.COMMENT_ACCESS_DENIED;
import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentFacadeService {
    private final CommentService commentService;
    private final ParticipantQueryService participantQueryService;
    private final AttachmentCommandService attachmentCommandService;
    private final MentionService mentionService;
    private final NotificationService notificationService;
    private final NotificationRecipientsService notificationRecipientsService;
    private final WorkService workService;
    private final TaskService taskService;
    private final TaskQueryService taskQueryService;
    private final S3Service s3Service;
    private final UserQueryService userQueryService;

    @Transactional
    public Long createComment(RequestCommentDTO commentDTO, Long taskId, Long userId) {

        /* 설명. 댓글 작성 순서 -> 댓글 작성, 첨부 파일 업로드, 알림에 추가 */
        taskService.validateTask(taskId);

        Long commentId = commentService.createComment(commentDTO, taskId, userId);
        Long projectId = taskQueryService.getProjectId(taskId);

        log.info("comment created with id: " + commentId);
        log.info("project created with id: " + projectId);

        if (projectId == null) {
            throw new BaseException(TASK_NOT_FOUND);
        }

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(COMMENT_ACCESS_DENIED);
        }


        if (commentDTO.getMentions() != null) {
            // 팀명만 태그했을 때
            // 전체 이름으로 태그했을 때 -> 파싱할 필요 없이 마이바티스 동적 쿼리로 사용
            // 어차피 회원 테이블에는 반정규화로 인해 부서명이 들어가있음

            List<Long> userIdByMention = userQueryService.selectIdByMentionList(commentDTO.getMentions());
            log.info("사용자 조회 완료");
            Long notificationId = notificationService.createMentionNotification(taskId);
            log.info("알림 생성 완료");
            notificationRecipientsService.createRecipientsByMention(userIdByMention, notificationId);
            log.info("알림 전달할 사람에게 전달 완료");
        }

        if (commentDTO.getDetails() != null) {
            log.info(commentDTO.getDetails().toString());

            List<Long> detailIdList = workService.selectWorkIdByName(commentDTO.getDetails());
            log.info("detailId: {}", detailIdList);

            // 최종 목적에 맞는 구조: 알림 ID → 수신자 ID 리스트
            Map<Long, List<Long>> notificationIdToUserIds = new HashMap<>();

            for (Long detailId : detailIdList) {
                log.info("loop 반복");
                // 알림 생성
                Long notificationId = notificationService.createDetailNotification(detailId);

                // detailId에 참여 중인 유저 목록 조회
                List<Long> participantIds = participantQueryService.selectParticipantsList(detailId);
                log.info("detailId={}, participants={}", detailId, participantIds);

                // 알림 ID와 해당 수신자 리스트를 매핑
                notificationIdToUserIds.put(notificationId, participantIds);
            }

            // 알림 수신자 등록
            notificationRecipientsService.createRecipients(notificationIdToUserIds);
        }


        /* 설명. 첨부 파일이 있을 때만 로직을 수행하게끔 흐름 조정 */
        if (commentDTO.getAttachmentFile() != null) {
            UploadFileResult uploadResult =
                    s3Service.uploadFile(commentDTO.getAttachmentFile(), "comment-docs");

            CreateAttachmentDTO attachmentDTO = new CreateAttachmentDTO(uploadResult);
            attachmentCommandService.createAttachmentForComment(attachmentDTO,
                    taskId,
                    userId);
        }
        return commentId;
    }

    @Transactional
    public Long deleteComment(Long userId, Long commentId) {;

        Long returnCommentId = commentService.updateByDelete(userId, commentId);
        return returnCommentId;
    }

    @Transactional
    public Long modifyComment(RequestModifyCommentDTO reqModify, Long userId, Long commentId) {
        return commentService.updateComment(reqModify, userId, commentId);
    }
}
