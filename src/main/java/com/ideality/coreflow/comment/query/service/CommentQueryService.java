package com.ideality.coreflow.comment.query.service;

import com.ideality.coreflow.comment.query.dto.ResponseCommentDTO;

import java.util.List;

public interface CommentQueryService {
    List<ResponseCommentDTO> selectComments(String taskId);
}
