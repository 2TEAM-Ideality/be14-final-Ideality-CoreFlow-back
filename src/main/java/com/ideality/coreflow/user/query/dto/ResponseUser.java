package com.ideality.coreflow.user.query.dto;

import lombok.Getter;

@Getter
public class ResponseUser {
    long id;
    String name;
    String deptName;
    String jobRankName;
    String jobRoleName;
    boolean isCreation;
    boolean isActive;
}
