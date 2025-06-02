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
import static com.ideality.coreflow.common.exception.ErrorCode.TASK_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public Long createTask(RequestTaskDTO taskDTO) {
        Work taskWork = Work.builder()
                .name(taskDTO.getLabel())
                .description(taskDTO.getDescription())
                .createdAt(LocalDateTime.now())
                .startBase(taskDTO.getStartBaseLine())
                .endBase(taskDTO.getEndBaseLine())
                .startExpect(taskDTO.getStartBaseLine())
                .endExpect(taskDTO.getEndBaseLine())
                .status(Status.PENDING)
                .projectId(taskDTO.getProjectId())
                .build();
        taskRepository.saveAndFlush(taskWork);
        log.info("Task created with id {}", taskWork.getId());
        return taskWork.getId();
    }

    @Override
    public void validateWorkId(Long prevWorkId, Long nextWorkId) {
        /* 설명.
         *  prevWorkId = 0, nextWorkId = null => 아무것도 없는 상황에서 태스크 생성
         *  prevWorkId = 값 존재, nextWorkId = null => 리프 노드
         *  prevWorkId = 값, nextWorkId = 값 -> 중간 노드
         *  prev == next -> 월요일에 수정해서 올리기
        * */

        if (prevWorkId != null && prevWorkId != 0 && !taskRepository.existsById(prevWorkId)) {
            throw new BaseException(TASK_NOT_FOUND);
        }

        if (prevWorkId != null && prevWorkId != 0
                && nextWorkId != null && !taskRepository.existsById(nextWorkId)) {
            throw new BaseException(TASK_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public Long updateStatusProgress(Long taskId) {
        Work updatedTask = taskRepository.findById(taskId).orElseThrow(() -> new BaseException(TASK_NOT_FOUND));

        updatedTask.startTask();
        return updatedTask.getId();
    }
}
