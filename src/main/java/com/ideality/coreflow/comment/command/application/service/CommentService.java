package com.ideality.coreflow.comment.command.application.service;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;

public interface CommentService {
    Long createComment(RequestCommentDTO commentDTO, Long taskId);
}
