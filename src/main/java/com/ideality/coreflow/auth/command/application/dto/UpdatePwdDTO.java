package com.ideality.coreflow.auth.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdatePwdDTO {
    long id;
    String prevPassword;
    String newPassword;
}
