package com.ideality.coreflow.mention.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.mention.dto.ResponseMentionDTO;
import com.ideality.coreflow.mention.service.facade.MentionFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mention")
@RequiredArgsConstructor
public class MentionController {

    private final MentionFacadeService mentionFacadeService;
    @GetMapping("/search")
    public ResponseEntity<APIResponse<List<ResponseMentionDTO>>> getMentionList(
            @RequestParam Long projectId,
            @RequestParam(required = false) String mentionTarget) {
        List<ResponseMentionDTO> getMentions = mentionFacadeService.getMentionList(projectId, mentionTarget);
        return ResponseEntity.ok(APIResponse.success(getMentions, "멘션 조회 성공"));
    }

    @GetMapping("/detail")
    public ResponseEntity<APIResponse<List<ResponseMentionDTO>>> getMentionDetail(
            @RequestParam Long projectId,
            @RequestParam Long taskId,
            @RequestParam(required = false) String detailTarget){
        List<ResponseMentionDTO> getDetails = mentionFacadeService.getDetailList(projectId,
                taskId,
                detailTarget);
        return ResponseEntity.ok(APIResponse.success(getDetails, "세부일정 태그 조회 성공"));
    }
}
