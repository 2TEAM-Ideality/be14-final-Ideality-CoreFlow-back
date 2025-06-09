package com.ideality.coreflow.email.command.application.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserLoginInfo {
    String employeeNum;
    String password;
    String email;
}
