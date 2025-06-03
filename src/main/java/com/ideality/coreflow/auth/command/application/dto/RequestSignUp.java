package com.ideality.coreflow.auth.command.application.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
public class RequestSignUp {
    private String name;
    private String email;
    private LocalDate birth;
    private LocalDate hireDate;
    private String deptName;
    private String jobRankName;
    private String jobRoleName;
    private boolean isCreation;
}
