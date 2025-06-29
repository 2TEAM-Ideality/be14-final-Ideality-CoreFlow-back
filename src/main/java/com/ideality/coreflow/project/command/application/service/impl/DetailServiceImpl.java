package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.holiday.query.service.HolidayQueryService;
import com.ideality.coreflow.project.command.application.dto.ParticipantDTO;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.service.*;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.ParticipantRepository;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final HolidayQueryService holidayQueryService;

    @Override
    @Transactional
    public Long createDetail(RequestDetailDTO detailDTO) {
        // 로그 객체 생성
        Logger log = LoggerFactory.getLogger(getClass());

        // RequestDetailDTO의 내용 로그 출력
        log.info("Request received for creating detail: projectId={}, parentTaskId={}, name={}, description={}, startBase={}, endBase={}, source={}, target={}, assigneeId={}, participantIds={}",
                detailDTO.getProjectId(),
                detailDTO.getParentTaskId(),
                detailDTO.getName(),
                detailDTO.getDescription(),
                detailDTO.getStartBase(),
                detailDTO.getEndBase(),
                detailDTO.getSource(),
                detailDTO.getTarget(),
                detailDTO.getAssigneeId(),
                detailDTO.getParticipantIds());

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

        log.info(requestDetailDTO.toString());
        // 기존 세부 일정 조회
        Work existingDetail = workRepository.findById(detailId).orElseThrow(() -> new BaseException(ErrorCode.RESOURCE_NOT_FOUND));

        if (existingDetail.getStatus() == Status.COMPLETED) {
            throw new BaseException(ErrorCode.STATUS_IS_COMPLETED);
        }

        // 지연일 계산 후 추가
        Long originalDuration = ChronoUnit.DAYS.between(existingDetail.getStartExpect(), existingDetail.getEndExpect())
                                +1
                                -holidayQueryService.countHolidaysBetween(existingDetail.getStartExpect(), existingDetail.getEndExpect());
        System.out.println("originalDuration = " + originalDuration);
        Long newDuration = ChronoUnit.DAYS.between(requestDetailDTO.getExpectStart(), requestDetailDTO.getExpectEnd())
                                +1
                                -holidayQueryService.countHolidaysBetween(requestDetailDTO.getExpectStart(), requestDetailDTO.getExpectEnd());
        System.out.println("newDuration = " + newDuration);
//        if(newDuration > originalDuration){
            System.out.println("existingDetail.getDelayDays() = " + existingDetail.getDelayDays());
            Long newDelayDays = existingDetail.getDelayDays() + (newDuration - originalDuration);
            System.out.println("newDelayDays = " + Math.toIntExact(newDelayDays));
            requestDetailDTO.setDelayDays(Math.toIntExact(newDelayDays));
            System.out.println("requestDetailDTO = " + requestDetailDTO);
//        }

        // 세부 일정 수정 (name, description, deptId 등)
        existingDetail.updateDetail(requestDetailDTO);

        // 세부 일정 저장
        workRepository.saveAndFlush(existingDetail);
        log.info("세부 일정 수정 완료");

        return existingDetail.getParentTaskId();
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
    public Long deleteDetail(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new BaseException(ErrorCode.DETAIL_NOT_FOUND));

        work.softDeleteTask();  // Work 엔티티에서 처리
       return workRepository.save(work).getParentTaskId();  // 업데이트된 Work 저장
    }
}
