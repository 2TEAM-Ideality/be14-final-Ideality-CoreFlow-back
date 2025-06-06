package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.DetailService;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




}
