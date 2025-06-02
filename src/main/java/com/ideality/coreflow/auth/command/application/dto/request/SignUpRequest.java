package com.ideality.coreflow.auth.command.application.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpRequest {
    private String name;
    private String email;
    private LocalDate birth;
    private LocalDate hireDate;
    private String deptName;
    private String jobRankName;
    private String jobRoleName;
    private boolean isCreation;
}
