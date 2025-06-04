package com.ideality.coreflow.comment.command.application.controller;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.service.facade.CommentFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacadeService commentFacadeService;

    @PostMapping
    public ResponseEntity<APIResponse<Map<String, Long>>> createComment(@RequestBody RequestCommentDTO commentDTO) {
        Long commentId = commentFacadeService.createComment(commentDTO);

        return null;
    }
}
