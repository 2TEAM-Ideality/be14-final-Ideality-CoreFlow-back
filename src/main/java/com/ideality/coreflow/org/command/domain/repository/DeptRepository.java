package com.ideality.coreflow.org.command.domain.repository;

import com.ideality.coreflow.org.command.domain.aggregate.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptRepository extends JpaRepository<Dept,Long> {
    List<Dept> findAllByIsDeletedFalse();
}
