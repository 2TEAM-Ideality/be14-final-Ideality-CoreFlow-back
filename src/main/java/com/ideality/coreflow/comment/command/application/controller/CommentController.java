package com.ideality.coreflow.comment.command.application.controller;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.service.facade.CommentFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacadeService commentFacadeService;

    @PostMapping("/{taskId}")
    public ResponseEntity<APIResponse<Map<String, Long>>>
    createComment(@RequestBody RequestCommentDTO commentDTO,
                  @PathVariable Long taskId) {
        Long commentId = commentFacadeService.createComment(commentDTO, taskId);

        return ResponseEntity.ok(APIResponse.success(Map.of("taskId", commentId),
                "댓글 작성이 완료되었습니다."));
    }
}
