package com.ideality.coreflow.admin.command.application.dto;

import lombok.Getter;

@Getter
public class RequestUserUpdateByAdmin {
    String name;
    String email;
    Boolean isResign;
    String profileImage;
    String deptName;
    String jobRankName;
    String jobRoleName;

    Boolean isCreation;
}
