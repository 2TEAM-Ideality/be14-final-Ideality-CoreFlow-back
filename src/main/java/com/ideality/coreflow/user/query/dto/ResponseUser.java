package com.ideality.coreflow.user.query.dto;

import lombok.Getter;

@Getter
public class ResponseUser {
    long id;
    String name;
    String deptName;
    String jobRankName;
    String jobRoleName;
    Boolean isCreation;
    Boolean isResign;
    Boolean isInner;
}
