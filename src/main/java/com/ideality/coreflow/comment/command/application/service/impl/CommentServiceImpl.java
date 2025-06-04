package com.ideality.coreflow.comment.command.application.service.impl;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.comment.command.domain.aggregate.Comment;
import com.ideality.coreflow.comment.command.domain.aggregate.CommentType;
import com.ideality.coreflow.comment.command.domain.repository.CommentRepository;
import com.ideality.coreflow.common.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ideality.coreflow.common.exception.ErrorCode.COMMENT_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Long createComment(RequestCommentDTO commentDTO, Long taskId) {

        if (!commentRepository.existsById(commentDTO.getParentCommentId())) {
            throw new BaseException(COMMENT_NOT_FOUND);
        }
        Comment newComment =
                Comment.builder()
                        .content(commentDTO.getContent())
                        .isDeleted(false)
                        .isNotice(commentDTO.getIsNotice())
                        .type(commentDTO.getIsNotice() ? CommentType.NOTICE: CommentType.COMMENT)
                        .userId(commentDTO.getUserId())
                        .workId(taskId)
                        .parentCommentId(commentDTO.getParentCommentId())
                        .build();
        commentRepository.save(newComment);
        return newComment.getId();
    }
}
