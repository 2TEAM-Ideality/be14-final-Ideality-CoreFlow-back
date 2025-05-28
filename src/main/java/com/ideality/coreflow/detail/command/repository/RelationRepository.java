package com.ideality.coreflow.detail.command.repository;

import com.ideality.coreflow.detail.command.entity.Relation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
}
