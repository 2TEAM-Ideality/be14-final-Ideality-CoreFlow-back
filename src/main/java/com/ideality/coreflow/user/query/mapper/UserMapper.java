package com.ideality.coreflow.user.query.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<Long> selectAllUserByDeptName(String deptName);

    List<Long> selectLeadersByDeptName(String deptName);

    String selectDeptNameByUserId(Long userId);
}
