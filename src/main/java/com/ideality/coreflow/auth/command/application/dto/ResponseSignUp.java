package com.ideality.coreflow.auth.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseSignUp {
    long id;
    String name;
    String deptName;
    String jobRankName;
    String jobRoleName;
    Boolean isCreation;
    Boolean isResign;
}
