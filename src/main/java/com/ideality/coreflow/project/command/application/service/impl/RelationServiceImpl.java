package com.ideality.coreflow.project.command.application.service.impl;

import com.ideality.coreflow.project.command.application.dto.EdgeInfoDTO;
import com.ideality.coreflow.project.command.application.dto.RequestRelationUpdateDTO;
import com.ideality.coreflow.project.command.application.service.RelationService;
import com.ideality.coreflow.project.command.domain.aggregate.Relation;
import com.ideality.coreflow.project.command.domain.aggregate.Work;
import com.ideality.coreflow.project.command.domain.repository.RelationRepository;
import com.ideality.coreflow.project.command.domain.repository.WorkRepository;
import com.ideality.coreflow.template.query.dto.EdgeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelationServiceImpl implements RelationService {

    private final RelationRepository relationRepository;
    private final WorkRepository taskRepository;

    @Override
    @Transactional
    public void createRelation(Long fromId, Long toId){
        System.out.println("✅RelationServiceImpl");
        System.out.println("fromId = " + fromId);
        System.out.println("toId = " + toId);
        Work fromWork = taskRepository.getReferenceById(fromId);
        Work toWork = taskRepository.getReferenceById(toId);
        Relation relation = Relation.builder()
                .prevWork(fromWork)
                .nextWork(toWork)
                .build();
        relationRepository.save(relation);
    }

    @Override
    @Transactional
    public void appendRelation(Long workId, List<Long> source, List<Long> target) {
        // source가 null -> 맨 앞에 넣을 것이지만, target과 관계를 이어간다.
        log.info("현재 들어온 값 {} {} {}",  workId, source, target);
        if (source == null) {
            for (Long targetWorkId : target) {

                Work prevWork = taskRepository.getReferenceById(workId);
                Work nextWork = taskRepository.getReferenceById(targetWorkId);
                Relation relation = Relation
                        .builder()
                        .prevWork(prevWork)
                        .nextWork(nextWork)
                        .build();

                relationRepository.save(relation);
            }
            // target이 null 앞에 관계가 있고 지금 생성한 작업을 넣는다 -> 수행한 작업을
        } else if (target == null) {
            for (Long sourceWorkId : source) {

                Work prevWork = taskRepository.getReferenceById(sourceWorkId);
                Work nextWork = taskRepository.getReferenceById(workId);
                Relation relation = Relation
                        .builder()
                        .prevWork(prevWork)
                        .nextWork(nextWork)
                        .build();

                relationRepository.save(relation);
            }
            // 둘 다 값이 존재할 때 -> source와 target과 workId를 전부 넘겨서 중간 삭제 및 새로 삽입 작업 수행
        } else {
            appendMiddleRelation(source, target, workId);
        }
    }
    
    @Override
    @Transactional
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


    /* 설명. 선행 일정이 해당 work 인거 삭제 -> Task, Detail 둘 다 사용 가능*/
    @Override
    @Transactional
    public void deleteByNextWorkId(Long workId) {
        relationRepository.deleteByNextWorkId(workId);
    }

    /* 설명. 선행 일정이 해당 work 인거 삭제 -> Task, Detail 둘 다 사용 가능*/
    @Override
    @Transactional
    public void deleteByPrevWorkId(Long workId) {
        relationRepository.deleteByPrevWorkId(workId);
    }


    /* 설명. 해당 태스크 별 삭제 + 처리까지 한번에 */
    @Override
    @Transactional
    public void updateRelationList(List<RequestRelationUpdateDTO> requestRelationUpdateDTO) {
        for (RequestRelationUpdateDTO updateDTO : requestRelationUpdateDTO) {
            Long taskId = updateDTO.getTaskId();
            List<Long> source = updateDTO.getSource();
            List<Long> target = updateDTO.getTarget();

            /* 설명. 반복해서 기존 태스크 id가 가진 선행 관계 + 후행 관계 삭제 */
            deleteByPrevWorkId(taskId);
            deleteByNextWorkId(taskId);

            appendRelation(taskId, source, target);
        }
    }

    @Override
    public List<EdgeInfoDTO> findTargetBySourceId(Long taskId) {
        List<Relation> relation = relationRepository.findByPrevWorkId(taskId);
        return relation.stream()
                .map(edge -> EdgeInfoDTO.builder()
                        .id(edge.getId())
                        .sourceId(edge.getPrevWork().getId())
                        .targetId(edge.getNextWork().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
