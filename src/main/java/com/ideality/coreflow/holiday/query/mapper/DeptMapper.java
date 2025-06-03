package com.ideality.coreflow.holiday.query.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface DeptMapper {
    Optional<Long> findIdByDeptName(String deptName);

    Optional<String> findDeptCodeByName(String deptName);
}
