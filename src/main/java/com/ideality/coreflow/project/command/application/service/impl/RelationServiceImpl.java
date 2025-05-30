package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.project.command.application.service.RelationService;
import com.ideality.coreflow.project.command.domain.aggregate.Relation;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.RelationRepository;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ideality.coreflow.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.ideality.coreflow.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final TaskRepository taskRepository;


    @Override
    @Transactional
    public void appendRelation(Long prevWorkId, Long nextWorkId, Long taskId) {

        if (prevWorkId != null && nextWorkId != null) {
            log.info("둘 다 값이 있는 상황에 왔음");
            int result = relationRepository.deleteByPrevWorkIdAndNextWorkId(prevWorkId, nextWorkId);

            if (result == 0) throw new BaseException(RESOURCE_NOT_FOUND);

            insertRelation(prevWorkId, taskId);
            insertRelation(taskId, prevWorkId);
        }

        if (prevWorkId == 0) {
            return;
        }
        log.info("리프 노드 같은 상황 - prev: {}, next: {}, task: {}", prevWorkId, nextWorkId, taskId);
        insertRelation(prevWorkId, taskId);
    }

    @Transactional
    public void insertRelation(Long prevId, Long nextId) {
        Work prevWork = taskRepository.getReferenceById(prevId);
        Work nextWork = taskRepository.getReferenceById(nextId);
        Relation relation = Relation
                .builder()
                .prevWork(prevWork)
                .nextWork(nextWork)
                .build();

        Relation saved = relationRepository.save(relation);
    }
}
