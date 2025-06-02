package com.ideality.coreflow.template.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ideality.coreflow.template.command.domain.aggregate.TemplateDept;

@Repository
public interface TemplateDeptRepository extends JpaRepository<TemplateDept, Long> {
}
