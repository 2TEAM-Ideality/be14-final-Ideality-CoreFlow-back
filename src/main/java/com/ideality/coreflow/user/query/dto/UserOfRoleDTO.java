package com.ideality.coreflow.user.query.dto;

import com.ideality.coreflow.user.command.domain.aggregate.RoleType;
import lombok.Getter;

@Getter
public class UserOfRoleDTO {

    Long roleId;
    Long userId;
    String roleName;
    RoleType roleType;
}
