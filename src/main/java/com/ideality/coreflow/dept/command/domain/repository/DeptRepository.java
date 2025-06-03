package com.ideality.coreflow.dept.command.domain.repository;

import com.ideality.coreflow.dept.command.domain.aggregate.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptRepository extends JpaRepository<Dept,Long> {
}
