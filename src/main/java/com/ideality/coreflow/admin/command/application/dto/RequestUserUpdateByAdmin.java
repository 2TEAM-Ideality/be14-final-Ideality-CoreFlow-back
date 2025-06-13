package com.ideality.coreflow.admin.command.application.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RequestUserUpdateByAdmin {
    String name;
    String email;
    LocalDate birth;
    LocalDate hireDate;
    Boolean isResign;
    LocalDate resignDate;
    String profileImage;
    String deptName;
    String jobRankName;
    String jobRoleName;

    Boolean isCreation;
}
