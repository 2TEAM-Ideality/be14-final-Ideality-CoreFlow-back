package com.ideality.coreflow.comment.query.service.facade;

import com.ideality.coreflow.attachment.query.dto.AttachmentForCommentDTO;
import com.ideality.coreflow.attachment.query.service.AttachmentQueryService;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.comment.query.dto.ResponseCommentForModifyDTO;
import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentQueryFacadeService {

    private final CommentQueryService commentQueryService;
    private final TaskQueryService taskQueryService;
    private final ParticipantQueryService participantQueryService;
    private final AttachmentQueryService attachmentQueryService;


    public List<SelectCommentDTO> selectComments(Long taskId, Long userId) {
        // 태스크에 따른 projectId 가져오기 + 예외 검사 (잘못된 태스크 Id)
        Long projectId = taskQueryService.selectProjectIdByTaskId(taskId);

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        /* 설명. 댓글 조회 */
        List<SelectCommentDTO> commentList = commentQueryService.selectComments(taskId);

        /* 설명. 댓글 Id 필터링 */
        List<Long> commentIds = commentList.stream()
                .map(SelectCommentDTO::getCommentId)
                .collect(Collectors.toList());

        log.info("Select comments for task {}, comment ids {}", taskId, commentIds);

        /* 설명. 첨부파일 조회? */
        List<AttachmentForCommentDTO> attachments = attachmentQueryService.selectAttachments(commentIds);
        log.info("Select comments for {} attachments", attachments);

        for (int i = 0; i < commentList.size(); i++) {
            SelectCommentDTO comment = commentList.get(i);
            log.info("Select comment {}", comment);

            if (i < attachments.size()) {
                AttachmentForCommentDTO attachment = attachments.get(i);

                if (attachment != null && attachment.getCommentId() != null &&
                        attachment.getCommentId().equals(comment.getCommentId())) {
                    log.info("Select attachment for comment {}", comment.getCommentId());
                    comment.setAttachmentId(attachment.getAttachmentId());
                    log.info("Select attachmentid {}", attachment.getAttachmentId());
                    comment.setOriginName(attachment.getOriginName());
                }
            }
        }
        // 검증 끝나면, 해당 댓글 찾아오기
        return commentList;
    }


    /* 설명. 공지 조회해오기 */
    public List<SelectCommentDTO> selectNotices(Long taskId, Long userId) {
        // 태스크에 따른 projectId 가져오기 + 예외 검사 (잘못된 태스크 Id)
        Long projectId = taskQueryService.selectProjectIdByTaskId(taskId);

        boolean isParticipant = participantQueryService.isParticipant(userId, projectId);

        if (!isParticipant) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        List<SelectCommentDTO> selectNoticeList = commentQueryService.selectNotices(taskId);


        /* 설명. 댓글 Id 필터링 */
        List<Long> commentIds = selectNoticeList.stream()
                .map(SelectCommentDTO::getCommentId)
                .collect(Collectors.toList());


        /* 설명. 첨부파일 조회? */
        List<AttachmentForCommentDTO> attachments = attachmentQueryService.selectAttachments(commentIds);
        log.info("Select comments for {} attachments", attachments);

        for (int i = 0; i < selectNoticeList.size(); i++) {
            SelectCommentDTO comment = selectNoticeList.get(i);

            if (i < attachments.size()) {
                AttachmentForCommentDTO attachment = attachments.get(i);

                if (attachment != null && attachment.getCommentId() != null &&
                        attachment.getCommentId().equals(comment.getCommentId())) {
                    log.info("Select attachment for comment {}", comment.getCommentId());
                    comment.setAttachmentId(attachment.getAttachmentId());
                    log.info("Select attachmentid {}", attachment.getAttachmentId());
                    comment.setOriginName(attachment.getOriginName());
                }
            }
        }
        // 검증 끝나면, 해당 공자 찾아오기
        return selectNoticeList;
    }

    /* 설명. 수정을 위해 전부 불러오는 부분 */
    public ResponseCommentForModifyDTO selectComment(Long commentId, Long userId) {
        ResponseCommentForModifyDTO resDTO = commentQueryService.selectComment(commentId, userId);
        AttachmentForCommentDTO attachment = attachmentQueryService.selectBycommentId(resDTO.getCommentId());

        if (attachment != null) {
            resDTO.setAttachmentId(attachment.getAttachmentId());
            resDTO.setOriginName(attachment.getOriginName());
        }

        return resDTO;
    }
}
