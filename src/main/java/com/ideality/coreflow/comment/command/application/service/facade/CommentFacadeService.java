package com.ideality.coreflow.comment.command.application.service.facade;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentFacadeService {
    private final CommentService commentService;
    private final ParticipantService participantService;
    public Long createComment(RequestCommentDTO commentDTO) {

        return null;
    }
}
