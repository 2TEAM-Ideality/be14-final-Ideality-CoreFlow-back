package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.service.DetailService;
import com.ideality.coreflow.project.command.application.service.ParticipantService;
import com.ideality.coreflow.project.command.application.service.RelationService;
import com.ideality.coreflow.project.command.application.service.WorkDeptService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.ParticipantRepository;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ideality.coreflow.common.exception.ErrorCode.DETAIL_NOT_FOUND;
import static com.ideality.coreflow.common.exception.ErrorCode.INVALID_SOURCE_LIST;
import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;


@Service
@RequiredArgsConstructor
@Slf4j
public class DetailServiceImpl implements DetailService {

    private final WorkRepository workRepository;
    private final ParticipantService participantService;
    private final RelationService relationService;
    private final WorkDeptService workDeptService;
    private final HolidayQueryService holidayQueryService;


    @Override
    public Double updateDetailPassedRate(Long detailId) {
        Work detailWork = workRepository.findById(detailId).orElseThrow(() -> new BaseException(DETAIL_NOT_FOUND));
        LocalDate now = LocalDate.now();
        LocalDate endBase = detailWork.getEndBase();
        LocalDate startBase = detailWork.getStartBase();
        LocalDate startReal = detailWork.getStartReal();

        Long totalDuration = ChronoUnit.DAYS.between(startBase, endBase)+1
                -holidayQueryService.countHolidaysBetween(startBase, endBase);
        Long passedDates = ChronoUnit.DAYS.between(startReal, now)+1
                -holidayQueryService.countHolidaysBetween(startReal, now);

        Double passedRate = (double) passedDates / totalDuration * 100;
        passedRate = passedRate>100?100:Math.round(passedRate*100)/100.0;

        detailWork.setPassedRate(passedRate);
        workRepository.save(detailWork);
        return detailWork.getPassedRate();
    }

    @Override
    @Transactional
    public Long createDetail(RequestDetailDTO detailDTO) {
        Work detailWork = Work.builder()
                .name(detailDTO.getName())
                .description(detailDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .startBase(detailDTO.getStartBase())
                .endBase(detailDTO.getEndBase())
                .startExpect(detailDTO.getStartBase())
                .endExpect(detailDTO.getEndBase())
                .status(Status.PENDING)
                .projectId(detailDTO.getProjectId())
                .parentTaskId(detailDTO.getParentTaskId()) // 추가
                .build();
        workRepository.saveAndFlush(detailWork);
        log.info("Detail created with id {}", detailWork.getId());
        return detailWork.getId();
    }

    @Override
    public void validateSource(List<Long> source) {
        if (source.isEmpty()) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        if (source.contains(null)){
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        for (Long sourceId : source) {
            if (sourceId != 0 && !workRepository.existsById(sourceId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }

    @Override
    public void validateTarget(List<Long> target){
        if (target.isEmpty()) {
            throw new BaseException(INVALID_SOURCE_LIST);
        }

        for (Long targetId : target) {
            if (!workRepository.existsById(targetId)) {
                throw new BaseException(TASK_NOT_FOUND);
            }
        }
    }



    @Transactional
    @Override
    public Long updateDetail(Long detailId, RequestDetailDTO requestDetailDTO) {
        // 기존 세부 일정 조회
        Optional<Work> existingDetailOptional = workRepository.findById(detailId);
        if (existingDetailOptional.isEmpty()) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);  // BaseException을 사용하여 리소스를 찾지 못했을 때 예외 처리
        }
        Work existingDetail = existingDetailOptional.get();

        // 세부 일정 수정 (name, description, deptId 등)
        existingDetail.setName(requestDetailDTO.getName());
        existingDetail.setDescription(requestDetailDTO.getDescription());
        existingDetail.setEndExpect(requestDetailDTO.getExpectEnd());
        existingDetail.setProgressRate(requestDetailDTO.getProgress());

        // 선행 일정 (source) 및 후행 일정 (target) 관계 수정
        //relationService.updateRelations(detailId, requestDetailDTO.getSource(), requestDetailDTO.getTarget());

        // 책임자DTO 생성해서 수정
        if (requestDetailDTO.getAssigneeId() != null) {
            ParticipantDTO assigneeDTO = ParticipantDTO.builder()
                    .targetType(TargetType.DETAILED)
                    .taskId(detailId)
                    .userId(requestDetailDTO.getAssigneeId())
                    .roleId(6L)  // 담당자 역할 ID
                    .build();
            participantService.updateAssignee(detailId, assigneeDTO);  // 담당자 수정
        }

        // 참여자 수정
        if (requestDetailDTO.getParticipantIds() != null && !requestDetailDTO.getParticipantIds().isEmpty()) {
            participantService.updateParticipants(detailId, requestDetailDTO.getParticipantIds());  // 참여자 수정
        }

        // 부서 수정
        if (requestDetailDTO.getDeptId() != null) {
            workDeptService.updateWorkDept(detailId, requestDetailDTO.getDeptId()); // 부서 수정
        }

        // 세부 일정 저장
        workRepository.save(existingDetail);
        log.info("세부 일정 수정 완료");

        return detailId;
    }


    // 1. 시작 버튼 (Status: PROGRESS, startReal: 현재 날짜)
    @Transactional
    @Override
    public void startDetail(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BaseException(ErrorCode.DETAIL_NOT_FOUND));

        work.startTask();  // Work 엔티티에서 처리
        workRepository.save(work);  // 업데이트된 Work 저장
    }

    // 2. 완료 버튼 (Status: COMPLETED, endReal: 현재 날짜, progressRate가 100일 경우)
    @Transactional
    @Override
    public void completeDetail(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() ->  new BaseException(ErrorCode.DETAIL_NOT_FOUND));

        if (work.getProgressRate() < 100) {
            throw new BaseException(ErrorCode.INVALID_STATUS_PROGRESS);
        }

        work.endTask();  // Work 엔티티에서 처리
        workRepository.save(work);  // 업데이트된 Work 저장
    }

    // 3. 삭제 버튼 (Status: DELETED)
    @Transactional
    @Override
    public void deleteDetail(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BaseException(ErrorCode.DETAIL_NOT_FOUND));

        work.softDeleteTask();  // Work 엔티티에서 처리
        workRepository.save(work);  // 업데이트된 Work 저장
    }




}
