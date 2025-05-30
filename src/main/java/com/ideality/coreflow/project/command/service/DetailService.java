package com.ideality.coreflow.project.command.service;

import com.ideality.coreflow.project.command.domain.aggregate.*;
import com.ideality.coreflow.project.command.domain.repository.*;
import com.ideality.coreflow.project.command.dto.DetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DetailService {

    private final WorkRepository workRepository;
    private final WorkDeptRepository workDeptRepository;
    private final DeptRepository deptRepository;
    private final ParticipantRepository participantRepository;
    private final RelationRepository relationRepository;

    @Autowired
    public DetailService(WorkRepository workRepository, WorkDeptRepository workDeptRepository,
                         DeptRepository deptRepository, ParticipantRepository participantRepository,RelationRepository relationRepository) {
        this.workRepository = workRepository;
        this.workDeptRepository = workDeptRepository;
        this.deptRepository = deptRepository;
        this.participantRepository = participantRepository;
        this.relationRepository = relationRepository;
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
        Work savedWork = workRepository.save(newWork);



        // 선행 작업 관계 등록 (Relation 테이블에 선행 작업 관계 추가)
        if (detailRequest.getPredecessorTaskId() != null) {
            Work predecessorTask = workRepository.findById(detailRequest.getPredecessorTaskId())
                    .orElseThrow(() -> new RuntimeException("선행 작업이 존재하지 않습니다."));

            Relation relation = new Relation();
            relation.setPrevWork(predecessorTask);  // 선행 작업 설정
            relation.setNextWork(savedWork);        // 후속 작업 설정
            relationRepository.save(relation);  // 관계 저장
        }

        // 후행 작업 관계 등록 (Relation 테이블에 후행 작업 관계 추가)
        if (detailRequest.getSuccessorTaskId() != null) {
            Work successorTask = workRepository.findById(detailRequest.getSuccessorTaskId())
                    .orElseThrow(() -> new RuntimeException("후행 작업이 존재하지 않습니다."));

            Relation relation = new Relation();
            relation.setPrevWork(savedWork);  // 후행 작업을 선행 작업으로 설정
            relation.setNextWork(successorTask); // 후행 작업 설정
            relationRepository.save(relation);  // 관계 저장
        }



        // 작업별 부서 등록(workDeptRepository)
        if (detailRequest.getDeptId() != null) {
            // DeptRepository를 사용해 부서를 조회
            Dept dept = deptRepository.findById(detailRequest.getDeptId())
                    .orElseThrow(() -> new RuntimeException("부서를 찾을 수 없습니다."));

            // WorkDept 테이블에 작업과 부서 관계 추가
            WorkDept workDept = new WorkDept();
            workDept.setWork(savedWork);  // 생성된 작업과 연결
            workDept.setDept(dept);  // 해당 부서와 연결
            workDeptRepository.save(workDept);  // 관계 저장
        }




        // 참여자 등록 (ParticipantRepository)
        if (detailRequest.getParticipantIds() != null) {
            for (Long userId : detailRequest.getParticipantIds()) {
                Participant participant = new Participant();
                participant.setUserId(userId);// 참여자의 userId 설정
                participant.setTargetType("DETAILED");  // 참여하는 대상 타입 설정 ("Work"라고 가정)
                participant.setTargetId(savedWork.getId());  // 작업의 ID를 targetId에 설정
                participant.setRoleId(7L);  // 참여자의 역할 아이디
                participantRepository.save(participant);
            }
        }

        // 책임자 등록 (ParticipantRepository)
        if (detailRequest.getAssigneeId() != null) {
            Participant assignee = new Participant();
            assignee.setUserId(detailRequest.getAssigneeId());
            assignee.setTargetType("DETAILED");  // 참여하는 대상 타입 설정 ("Work"라고 가정)
            assignee.setTargetId(savedWork.getId());  // 작업의 ID를 targetId에 설정
            assignee.setRoleId(6L);  // 참여자의 역할 아이디
            participantRepository.save(assignee);
        }
        return savedWork;
    }
}
