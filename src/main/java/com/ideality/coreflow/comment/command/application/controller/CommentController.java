package com.ideality.coreflow.comment.command.application.controller;

import com.ideality.coreflow.comment.command.application.dto.RequestCommentDTO;
import com.ideality.coreflow.comment.command.application.dto.RequestModifyCommentDTO;
import com.ideality.coreflow.comment.command.application.service.facade.CommentFacadeService;
import com.ideality.coreflow.common.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacadeService commentFacadeService;

    @PostMapping(value = "/write/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<Map<String, Long>>>
    createComment(@ModelAttribute RequestCommentDTO commentDTO,
                  @PathVariable Long taskId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long commentId = commentFacadeService.createComment(commentDTO, taskId, userId);

        return ResponseEntity.ok(APIResponse.success(Map.of("commentId", commentId),
                "댓글 작성이 완료되었습니다."));
    }

    @PatchMapping(value = "/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<Map<String, Long>>> modifyComment
            (@ModelAttribute RequestModifyCommentDTO reqModify,
             @PathVariable Long commentId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long returnCommentId = commentFacadeService.modifyComment(reqModify, userId, commentId);
        return ResponseEntity.ok(APIResponse.success(Map.of("commentId", returnCommentId),
                "댓글이 수정되었습니다."));
    }

    @PatchMapping(value = "/{commentId}/delete")
    public ResponseEntity<APIResponse<Map<String, Long>>> deleteCommentBySoft
            (@PathVariable Long commentId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long returnCommentId = commentFacadeService.deleteComment(userId, commentId);
        return ResponseEntity.ok(APIResponse.success(Map.of("commentId", returnCommentId),
                "댓글이 삭제되었습니다."));
    }

    @PatchMapping(value = "/{commentId}/notice")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateCommentNotice
            (@PathVariable Long commentId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long returnCommentId = commentFacadeService.updateCommentByNotice(userId, commentId);
        return ResponseEntity.ok(APIResponse.success(Map.of("commentId", returnCommentId)
        , "공지로 업데이트 되었습니다!"));
    }
}
