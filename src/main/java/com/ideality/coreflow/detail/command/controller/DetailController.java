package com.ideality.coreflow.detail.command.controller;

import com.ideality.coreflow.detail.command.dto.DetailRequest;
import com.ideality.coreflow.detail.command.entity.Work;
import com.ideality.coreflow.detail.command.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/detail")
public class DetailController {

    private final DetailService detailService;

    @Autowired
    public DetailController(DetailService detailService) {
        this.detailService = detailService;
    }

    @PostMapping("/create")
    public Work createWorkWithDeptAndParticipants(@RequestBody DetailRequest detailRequest) {
        // WorkRequest DTO를 통해 데이터 받기
        Work work = new Work();
        work.setName(detailRequest.getName());
        work.setDescription(detailRequest.getDescription());
        work.setCreatedAt(new Date());
        work.setStartBase(detailRequest.getStartBase());
        work.setEndBase(detailRequest.getEndBase());
        work.setProgressRate(0.0);
        work.setPassedRate(0.0);
        work.setStatus("PENDING");

        // 부서 ID와 사용자 ID를 받아서 작업 생성
        return detailService.createWorkWithDeptAndParticipants(work, detailRequest.getDeptId(), detailRequest.getUserId());
    }
}