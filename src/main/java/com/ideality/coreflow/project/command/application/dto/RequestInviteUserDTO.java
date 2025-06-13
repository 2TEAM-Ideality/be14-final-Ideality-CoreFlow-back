package com.ideality.coreflow.project.command.application.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestInviteUserDTO {
    private Long userId;
    private String deptName;
}
