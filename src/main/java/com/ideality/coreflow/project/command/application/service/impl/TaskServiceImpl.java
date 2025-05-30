package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;
import com.ideality.coreflow.project.command.application.service.TaskService;
import com.ideality.coreflow.project.command.domain.aggregate.Status;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ideality.coreflow.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public Long createTask(RequestTaskDTO taskDTO) {
        Work taskWork = Work.builder()
                .name(taskDTO.getTaskName())
                .description(taskDTO.getTaskDescription())
                .createdAt(LocalDateTime.now())
                .startBase(taskDTO.getStartBase())
                .endBase(taskDTO.getEndBase())
                .startExpect(taskDTO.getStartBase())
                .endExpect(taskDTO.getEndBase())
                .status(Status.PENDING)
                .projectId(taskDTO.getProjectId())
                .build();
        taskRepository.save(taskWork);
        log.info("Task created with id {}", taskWork.getId());
        return taskWork.getId();
    }

    @Override
    public void validateWorkId(Long prevWorkId, Long nextWorkId) {
        /* 설명.
         *  prevWorkId = 0, nextWorkId = null => 아무것도 없는 상황에서 태스크 생성
         *  prevWorkId = 값 존재, nextWorkId = null => 리프 노드
         *  prevWorkId = 값, nextWorkId = 값 -> 중간 노드
        * */

        if (prevWorkId == null) {
            throw new BaseException(RESOURCE_NOT_FOUND); // or CUSTOM_BAD_REQUEST
        }

        // Case: 첫 태스크 생성 (prev=0, next=null)
        if (prevWorkId == 0 && nextWorkId == null) {
            return; // 검증 필요 없음
        }

        // Case: prevWork 존재해야 함
        if (prevWorkId != 0) {
            taskRepository.findById(prevWorkId)
                    .orElseThrow(() -> new BaseException(RESOURCE_NOT_FOUND));
        }

        // Case: nextWork 존재 시 검증
        if (nextWorkId != null) {
            taskRepository.findById(nextWorkId)
                    .orElseThrow(() -> new BaseException(RESOURCE_NOT_FOUND));
        }
    }
}
