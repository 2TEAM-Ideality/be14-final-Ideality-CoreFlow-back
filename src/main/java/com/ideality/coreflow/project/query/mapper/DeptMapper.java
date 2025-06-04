package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DeptMapper {
    Optional<String> findNameById(Long id);

    Optional<String> findDeptCodeByName(String deptName);

    // 모든 부서명 조회 (XML 쿼리와 연결)
    List<String> findAllDeptNames();

}
