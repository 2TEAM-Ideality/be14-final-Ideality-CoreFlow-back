package com.ideality.coreflow.user.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    List<Long> selectAllUserByDept(String deptName);
}
