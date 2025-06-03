package com.ideality.coreflow.user.command.application.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInfoDTO {
    private String employeeNum;
    private String password;
    private String name;
    private String email;
    private LocalDate birth;
    private LocalDate hireDate;
    private Boolean isResign;
    private LocalDate resignDate;
    private String profileImage;
    private String deptName;
    private String jobRankName;
    private String jobRoleName;
}
