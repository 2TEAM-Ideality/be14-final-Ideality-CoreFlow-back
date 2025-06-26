package com.ideality.coreflow.comment.command.application.service;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestModifyCommentDTO;
import com.ideality.coreflow.comment.command.domain.aggregate.Comment;

public interface CommentService {
    Long createComment(RequestCommentDTO commentDTO, Long taskId, Long userId);

    Long updateByDelete(Long userId, Comment comment);

    Long updateComment(RequestModifyCommentDTO reqModify, Long userId, Comment comment);

    Long updateByNotice(Long userId, Comment commentId);

    Comment findById(Long commentId);
}
