package com.ideality.coreflow.project.query.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper {
    Long findByIdDeptName(String deptName);
}
