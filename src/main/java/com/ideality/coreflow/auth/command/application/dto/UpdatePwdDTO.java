package com.ideality.coreflow.auth.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePwdDTO {
    String employeeNum;
    String prevPassword;
    String newPassword;
}
