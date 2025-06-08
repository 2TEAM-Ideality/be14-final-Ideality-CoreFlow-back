package com.ideality.coreflow.auth.command.application.dto;

import lombok.Getter;

@Getter
public class RequestSignUpPartner {
    String name;
    String email;
    String roleName = "협력업체";
}
