package com.ideality.coreflow.mention.controller;

import com.ideality.coreflow.common.response.APIResponse;
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
    public ResponseEntity<APIResponse<Map<String, List<String>>>> getMentionList(
            @RequestParam Long projectId,
            @RequestParam(required = false) String mentionTarget) {
        List<String> getMentions = mentionFacadeService.getMentionList(projectId, mentionTarget);
        return ResponseEntity.ok(APIResponse.success(Map.of("mentions", getMentions),
                "멘션 조회에 성공하였습니다."));
    }
}
