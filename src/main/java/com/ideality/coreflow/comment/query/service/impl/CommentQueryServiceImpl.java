package com.ideality.coreflow.comment.query.service.impl;

import com.ideality.coreflow.comment.query.dto.ResponseCommentForModifyDTO;
import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;
import com.ideality.coreflow.comment.query.mapper.CommentMapper;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentQueryServiceImpl implements CommentQueryService {
    private final CommentMapper commentMapper;

    @Override
    public List<SelectCommentDTO> selectComments(Long taskId) {
        return commentMapper.selectComments(taskId);
    }

    @Override
    public ResponseCommentForModifyDTO selectComment(Long commentId, Long userId) {
        ResponseCommentForModifyDTO dto = commentMapper.selectCommentByModify(commentId);

        if (dto == null) {
            throw new BaseException(ErrorCode.COMMENT_NOT_FOUND);
        }

        if (!dto.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        return dto;
    }

    @Override
    public List<SelectCommentDTO> selectNotices(Long taskId) {
        return commentMapper.selectNotices(taskId);
    }
}
