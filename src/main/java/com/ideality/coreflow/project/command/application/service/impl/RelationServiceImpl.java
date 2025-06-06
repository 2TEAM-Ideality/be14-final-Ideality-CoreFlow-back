package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.project.command.application.service.RelationService;
import com.ideality.coreflow.project.command.domain.aggregate.Relation;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.RelationRepository;
import com.ideality.coreflow.project.command.domain.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final TaskRepository taskRepository;


    @Override
    @Transactional
    public void appendRelation(List<Long> prevWorkId, Long nextWorkId) {

        for (Long workId : prevWorkId) {

            Work prevWork = taskRepository.getReferenceById(workId);
            Work nextWork = taskRepository.getReferenceById(nextWorkId);
            Relation relation = Relation
                    .builder()
                    .prevWork(prevWork)
                    .nextWork(nextWork)
                    .build();

            relationRepository.save(relation);
        }
    }

    @Override
    public void appendMiddleRelation(List<Long> source, List<Long> target, Long taskId) {
        for (Long sourceId : source) {
            for (Long targetId : target) {
                relationRepository.deleteByPrevWorkIdAndNextWorkId(sourceId, targetId);
            }

            /* 설명. 일차 저장 */
            Work prevWork = taskRepository.getReferenceById(sourceId);
            Work nextWork = taskRepository.getReferenceById(taskId);
            Relation relation = Relation
                    .builder()
                    .prevWork(prevWork)
                    .nextWork(nextWork)
                    .build();

            relationRepository.save(relation);
        }

        for (Long targetId : target) {
            Work prevWork = taskRepository.getReferenceById(taskId);
            Work nextWork = taskRepository.getReferenceById(targetId);
            Relation relation = Relation
                    .builder()
                    .prevWork(prevWork)
                    .nextWork(nextWork)
                    .build();

            relationRepository.save(relation);
        }
    }


    @Override
    @Transactional
    public void appendTargetRelation(List<Long> target, Long taskId) {
        for (Long targetId : target) {
            Work prevWork = taskRepository.getReferenceById(taskId); // 현재 작업이 이전 작업
            Work nextWork = taskRepository.getReferenceById(targetId); // target 작업이 다음 작업

            Relation relation = Relation
                    .builder()
                    .prevWork(prevWork)
                    .nextWork(nextWork)
                    .build();

            relationRepository.save(relation);
        }
    }

}
