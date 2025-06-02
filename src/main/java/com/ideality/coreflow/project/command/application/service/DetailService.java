package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.command.domain.aggregate.*;
import com.ideality.coreflow.project.command.domain.repository.*;
import com.ideality.coreflow.project.command.application.dto.DetailRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.ideality.coreflow.project.command.domain.aggregate.TargetType.DETAILED;

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
    @Transactional
    public Work createWorkWithDeptAndParticipants(DetailRequest detailRequest) {

        if (detailRequest.getParentTaskId() != null) {
            Optional<Work> parentTask = workRepository.findById(detailRequest.getParentTaskId());
            if (!parentTask.isPresent()) {
                throw new BaseException(ErrorCode.PARENT_TASK_NOT_FOUND); // 예외 처리
            }
        }


        // DTO 에서 엔티티 로 변환 (빌더 사용)
        Work newWork = Work.builder()
                .name(detailRequest.getName())
                .description(detailRequest.getDescription())
                .startBase(detailRequest.getStartBase())
                .endBase(detailRequest.getEndBase())
                .startExpect(detailRequest.getStartBase())
                .endExpect(detailRequest.getEndBase())
                .createdAt(LocalDateTime.now())
                .status(Status.PENDING)
                .parentTaskId(detailRequest.getParentTaskId())//태스크와 다른 점!
                .projectId(detailRequest.getProjectId())
                .build();
        // 세부 작업 저장
        Work savedWork = workRepository.save(newWork);



        // relation 테이블에 관계 저장
        if (detailRequest.getPredecessorTaskId() != null && detailRequest.getSuccessorTaskId() != null) {

            // 선행 작업과 후행 작업을 조회
            Work predecessorTask = workRepository.findById(detailRequest.getPredecessorTaskId())
                    .orElseThrow(() -> new BaseException(ErrorCode.PREDECESSOR_NOT_FOUND));

            Work successorTask = workRepository.findById(detailRequest.getSuccessorTaskId())
                    .orElseThrow(() -> new BaseException(ErrorCode.SUCCESSOR_NOT_FOUND));

            // Relation 테이블에 선행 작업과 후행 작업 관계를 설정
            Relation relation = new Relation();
            relation.setPrevWork(predecessorTask);  // 선행 작업 설정
            relation.setNextWork(successorTask);    // 후행 작업 설정
            relationRepository.save(relation);  // 관계 저장
        } else {
            throw new BaseException(ErrorCode.PREDECESSOR_AND_SUCCESSOR_REQUIRED);  // 선행 작업과 후행 작업이 모두 필요하다는 예외
        }



        // 작업별 부서 등록(workDeptRepository)
        if (detailRequest.getDeptId() != null) {
            // DeptRepository를 사용해 부서를 조회
            Dept dept = deptRepository.findById(detailRequest.getDeptId())
                    .orElseThrow(() -> new BaseException(ErrorCode.DEPT_NOT_FOUND)); // 부서가 존재하지 않으면 예외 던지기

            // WorkDept 테이블에 작업과 부서 관계 추가
            WorkDept workDept = new WorkDept();
            workDept.setWork(savedWork);  // 생성된 작업과 연결
            workDept.setDept(dept);  // 해당 부서와 연결
            workDeptRepository.save(workDept);  // 관계 저장
        }



        //로직이 다른 것은 participant를 거기서는 걍 부서만 등록하는데 난 다 직접 지정함..
        // 회원에서 부서 이름으로 모든 회원 조회 이 부분은 그래서 나는 빠져도됨
        //그럼 나느 어떻게.. ? participant 분리시 taskId가져올까? 비슷하게 다 그냥 파사드서비스에서 가져오면 됨!!

        // 참여자 등록 (ParticipantRepository)
        if (detailRequest.getParticipantIds() != null) {
            for (Long userId : detailRequest.getParticipantIds()) {


                Participant participant = new Participant();
                participant.setUserId(userId);// 참여자의 userId 설정
                participant.setTargetType(DETAILED);  // 참여하는 대상 타입 설정
                participant.setTargetId(savedWork.getId());  // 작업의 ID를 targetId에 설정
                participant.setRoleId(7L);  // 참여자의 역할 아이디
                participantRepository.save(participant);
            }
        }

        // 책임자 등록 (ParticipantRepository)
        if (detailRequest.getAssigneeId() != null) {
            Participant assignee = new Participant();
            assignee.setUserId(detailRequest.getAssigneeId());
            assignee.setTargetType(DETAILED);  // 참여하는 대상 타입 설정 ("Work"라고 가정)
            assignee.setTargetId(savedWork.getId());  // 작업의 ID를 targetId에 설정
            assignee.setRoleId(6L);  // 참여자의 역할 아이디
            participantRepository.save(assignee);
        }
        return savedWork;
    }
}
