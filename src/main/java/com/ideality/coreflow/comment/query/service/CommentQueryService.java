package com.ideality.coreflow.comment.query.service;

import com.ideality.coreflow.comment.query.dto.ResponseCommentForModifyDTO;
import com.ideality.coreflow.comment.query.dto.ResponseCommentsDTO;

import java.util.List;

public interface CommentQueryService {
    List<ResponseCommentsDTO> selectComments(Long taskId);

    ResponseCommentForModifyDTO selectComment(Long commentId, Long userId);

    List<ResponseCommentsDTO> selectNotices(Long taskId);
}
