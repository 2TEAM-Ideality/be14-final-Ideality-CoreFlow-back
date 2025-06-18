package com.ideality.coreflow.auth.command.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class ResponseTokenAndUserInfo {
    Long id;
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

    String accessToken;
    String refreshToken;
    String schemaName;
    List<String> roles;
    boolean isTemp;

    public void deleteRefreshToken() {
        this.refreshToken = null;
    }
}
