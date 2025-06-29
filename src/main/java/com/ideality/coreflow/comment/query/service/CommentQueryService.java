package com.ideality.coreflow.comment.query.service;

import com.ideality.coreflow.attachment.query.dto.GetTaskInfoDTO;
import com.ideality.coreflow.comment.query.dto.ResponseCommentForModifyDTO;
import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;

import java.util.List;

public interface CommentQueryService {
    List<SelectCommentDTO> selectComments(Long taskId);

    ResponseCommentForModifyDTO selectComment(Long commentId, Long userId);

    List<SelectCommentDTO> selectNotices(Long taskId);

    List<Long> selectAllCommentsByAttachment(Long taskId);

    List<GetTaskInfoDTO> selectAllCommentsByTaskList(Long taskIdList);
}
