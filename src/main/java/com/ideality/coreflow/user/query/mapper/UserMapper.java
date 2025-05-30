package com.ideality.coreflow.user.query.mapper;

import com.ideality.coreflow.user.query.dto.ParticipantUserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<ParticipantUserDTO> selectAllUserByDept(String deptName);
}
