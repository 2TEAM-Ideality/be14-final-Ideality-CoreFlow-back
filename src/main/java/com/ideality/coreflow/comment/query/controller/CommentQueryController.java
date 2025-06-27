package com.ideality.coreflow.comment.query.controller;

import com.ideality.coreflow.comment.query.dto.ResponseCommentForModifyDTO;
import com.ideality.coreflow.comment.query.dto.SelectCommentDTO;
import com.ideality.coreflow.comment.query.service.CommentQueryService;
import com.ideality.coreflow.comment.query.service.facade.CommentQueryFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentQueryController {

    private final CommentQueryFacadeService commentQueryFacadeService;

    @GetMapping("/task/{taskId}")
    public ResponseEntity<APIResponse<List<SelectCommentDTO>>>
    getComments(@PathVariable Long taskId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<SelectCommentDTO> resComment = commentQueryFacadeService.selectComments(taskId, userId);
        return ResponseEntity.ok(APIResponse.success(resComment, "댓글 목록 조회에 성공하였습니다."));
    }

    // 댓글 수정하기 누르면 댓글 해당 정보가 불러와짐
    @GetMapping("/{commentId}")
    public ResponseEntity<APIResponse<ResponseCommentForModifyDTO>> getCommentContent(@PathVariable Long commentId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        ResponseCommentForModifyDTO resDTO = commentQueryFacadeService.selectComment(commentId, userId);
        return ResponseEntity.ok(APIResponse.success(resDTO, "댓글 내용 조회에 성공하였습니다."));
    }

    @GetMapping("/task/{taskId}/notice")
    public ResponseEntity<APIResponse<List<SelectCommentDTO>>>
    getNotices(@PathVariable Long taskId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        List<SelectCommentDTO> resComment = commentQueryFacadeService.selectNotices(taskId, userId);
        return ResponseEntity.ok(APIResponse.success(resComment, "댓글 목록 조회에 성공하였습니다."));
    }
}
