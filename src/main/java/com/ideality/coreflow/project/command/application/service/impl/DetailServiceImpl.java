package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.service.DetailService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.ideality.coreflow.common.exception.ErrorCode.INVALID_SOURCE_LIST;
import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;


@Service
@RequiredArgsConstructor
@Slf4j
public class DetailServiceImpl implements DetailService {

    private final WorkRepository workRepository;

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


}
