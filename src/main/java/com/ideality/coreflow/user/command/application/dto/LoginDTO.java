package com.ideality.coreflow.user.command.application.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDTO {
    Long id;
    String employeeNum;
    String password;
}
