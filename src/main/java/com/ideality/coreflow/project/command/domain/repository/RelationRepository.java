package com.ideality.coreflow.project.command.domain.repository;


import com.ideality.coreflow.project.command.domain.aggregate.Relation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
    void deleteByPrevWorkIdAndNextWorkId(Long prevWorkId, Long nextWorkId);
    void deleteByPrevWorkId(Long prevWorkId);  // prevWorkId에 해당하는 모든 관계 삭제
    void deleteByNextWorkId(Long nextWorkId);  // nextWorkId에 해당하는 모든 관계 삭제

}
