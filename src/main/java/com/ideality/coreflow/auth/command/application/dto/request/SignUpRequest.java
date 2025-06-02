package com.ideality.coreflow.auth.command.application.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpRequest {
    private String id;
    private String employeeNum;
    private String password;
    private String name;
    private String email;
    private LocalDate birth;
    private LocalDate hireDate;
    private Boolean isResign;
    private String deptName;
    private String JobRankName;
    private String JobRoleName;
}
