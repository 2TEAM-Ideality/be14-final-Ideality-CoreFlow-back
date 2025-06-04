package com.ideality.coreflow.user.query.mapper;

import com.ideality.coreflow.user.query.dto.UserNameIdDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<Long> selectAllUserByDeptName(String deptName);

    // 이름으로 회원 조회 (id와 name만 반환)
    List<UserNameIdDto> searchUsersByName(String name);

    List<Long> selectLeadersByDeptName(String deptName);

}
