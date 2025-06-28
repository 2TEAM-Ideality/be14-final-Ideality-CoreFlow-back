package com.ideality.coreflow.project.command.application.controller;

import com.ideality.coreflow.common.response.APIResponse;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.service.facade.ProjectFacadeService;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Logger log = LoggerFactory.getLogger(getClass());
        // 이때 RequestBody가 제대로 바인딩되는지 확인
        log.info("Request received: {}", requestDetailDTO);

        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Long detailId = projectFacadeService.createDetail(requestDetailDTO, userId);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("detailId", detailId),
                        "세부일정이 성공적으로 생성되었습니다.")
        );
    }

    // 세부일정 수정
    @PutMapping("/update/{detailId}")
    public ResponseEntity<APIResponse<Map<String, Long>>> updateDetailWithFacade(
            @PathVariable Long detailId , @RequestBody RequestDetailDTO requestDetailDTO) {
        System.out.println("requestDetailDTO = " + requestDetailDTO);

        // 퍼사드 서비스의 updateDetail 호출
        Long updatedDetailId = projectFacadeService.updateDetail(detailId, requestDetailDTO);

        // 성공적인 응답 반환
        return ResponseEntity.ok(
                APIResponse.success(Map.of("detailId", updatedDetailId),
                        "세부일정이 성공적으로 수정되었습니다.")
        );
    }


    // 1. 시작 버튼 (Status: PROGRESS, startReal: 현재 날짜)
    @PatchMapping("/{workId}/start")
    public ResponseEntity<APIResponse<String>> startTask(@PathVariable Long workId) {
        projectFacadeService.startDetail(workId);
        return ResponseEntity.ok(
                APIResponse.success("Detail started successfully", "세부일정이 시작되었습니다.")
        );
    }

    // 2. 완료 버튼 (Status: COMPLETED, endReal: 현재 날짜, progressRate가 100일 경우)
    @PatchMapping("/{workId}/complete")
    public ResponseEntity<APIResponse<String>> completeTask(@PathVariable Long workId) {
        projectFacadeService.completeDetail(workId);
        return ResponseEntity.ok(
                APIResponse.success("Detail completed successfully", "세부일정이 완료되었습니다.")
        );
    }

    // 3. 삭제 버튼 (Status: DELETED)
    @PatchMapping("/{workId}/delete")
    public ResponseEntity<APIResponse<String>> deleteTask(@PathVariable Long workId) {
        projectFacadeService.deleteDetail(workId);
        return ResponseEntity.ok(
                APIResponse.success("Detail deleted successfully", "세부일정이 삭제되었습니다.")
        );
    }

    @PatchMapping("{workId}/passed-rate")
    public ResponseEntity<APIResponse<Map<String, Object>>> updateDetailPassedRate(@PathVariable Long workId){
        Double updatedPassedRate = projectFacadeService.updatePassedRate(workId, TargetType.DETAILED);
        return ResponseEntity.ok(
                APIResponse.success(Map.of("updatedPassedRate", updatedPassedRate),
                        workId +"번 세부일정의 경과율이 업데이트 되었습니다.")
        );
    }

}
