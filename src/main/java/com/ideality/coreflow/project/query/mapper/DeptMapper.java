package com.ideality.coreflow.project.query.mapper;

import com.ideality.coreflow.project.query.dto.ResponseDeptIdDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper {
    ResponseDeptIdDTO findIdByDeptName(String deptName);
}
