package com.ideality.coreflow.project.command.service;

import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.project.command.dto.DetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DetailService {

    private final WorkRepository workRepository;

    @Autowired
    public DetailService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }


    // 클라이언트가 보내는 작업 및 부서, 사용자 정보를 기반으로 세부 일정을 생성
    public Work createWorkWithDeptAndParticipants(DetailRequest detailRequest) {


        // DTO 에서 엔티티 로 변환 (빌더 사용)
        Work newWork = Work.builder()
                .name(detailRequest.getName())
                .description(detailRequest.getDescription())
                .startBase(detailRequest.getStartBase())
                .endBase(detailRequest.getEndBase())
                .startExpect(detailRequest.getStartBase())
                .endExpect(detailRequest.getEndBase())
                .createdAt(LocalDateTime.now())  // createdAt은 현재 날짜로 설정
                .progressRate(0.0)
                .passedRate(0.0)
                .status(Status.PENDING)  // Enum이 존재한다고 가정
                .slackTime(0)
                .parentTaskId(detailRequest.getParentTaskId())
                .projectId(detailRequest.getProjectId())  // 프로젝트 ID도 포함될 경우
                .build();
        // 세부 작업 저장
        return workRepository.save(newWork);
    }
}
