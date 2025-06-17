package com.ideality.coreflow.comment.command.application.service;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestModifyCommentDTO;
import com.ideality.coreflow.comment.command.domain.aggregate.Comment;

import java.util.Optional;

public interface CommentService {
    Long createComment(RequestCommentDTO commentDTO, Long taskId, Long userId);

    Long updateByDelete(Long userId, Long commentId);

    Long updateComment(RequestModifyCommentDTO reqModify, Long userId, Long commentId);

    Long updateByNotice(Long userId, Long commentId);

    Comment findById(Long commentId);
}
