package com.ideality.coreflow.project.command.domain.repository;


import com.ideality.coreflow.project.command.domain.aggregate.Relation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
    void deleteByPrevWorkIdAndNextWorkId(Long prevWorkId, Long nextWorkId);
}
