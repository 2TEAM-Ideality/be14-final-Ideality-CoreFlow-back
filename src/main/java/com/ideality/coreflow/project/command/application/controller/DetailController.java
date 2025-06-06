package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.DetailService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detail")
public class DetailController {

    private final ProjectFacadeService projectFacadeService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Map<String, Long>>> createDetailWithFacade(
            @RequestBody RequestDetailDTO requestDetailDTO) {
        Long detailId = projectFacadeService.createDetail(requestDetailDTO);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("detailId", detailId),
                        "세부일정이 성공적으로 생성되었습니다.")
        );
    }

    // 세부일정 수정
    @PutMapping("/update/{detailId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateDetailWithFacade(
            @PathVariable Long detailId , @RequestBody RequestDetailDTO requestDetailDTO) {

        // 퍼사드 서비스의 updateDetail 호출
        Long updatedDetailId = projectFacadeService.updateDetail(detailId, requestDetailDTO);

        // 성공적인 응답 반환
        return ResponseEntity.ok(
                APIResponse.success(Map.of("detailId", updatedDetailId),
                        "세부일정이 성공적으로 수정되었습니다.")
        );
    }



}
