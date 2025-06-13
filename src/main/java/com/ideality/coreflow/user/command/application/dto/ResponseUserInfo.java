package com.ideality.coreflow.user.command.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ResponseUserInfo {
    long id;
    String employeeNum;
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

    List<String> roles;
}
