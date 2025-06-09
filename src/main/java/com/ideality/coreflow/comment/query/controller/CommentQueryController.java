package com.ideality.coreflow.comment.query.controller;

import com.ideality.coreflow.comment.query.dto.ResponseCommentDTO;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryService commentQueryService;

    @GetMapping("/{taskId}")
    public ResponseEntity<APIResponse<List<ResponseCommentDTO>>> getComment(@PathVariable String taskId) {
        List<ResponseCommentDTO> resComment = commentQueryService.selectComments(taskId);
        return ResponseEntity.ok(APIResponse.success(resComment, "댓글 조회에 성공하였습니다."));
    }
}
