package com.ideality.coreflow.auth.command.application.dto;

import lombok.Getter;

@Getter
public class RequestUpdatePwd {
    String prevPassword;
    String newPassword;
}
