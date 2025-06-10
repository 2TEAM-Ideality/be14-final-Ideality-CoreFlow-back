package com.ideality.coreflow.project.command.application.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestTeamLeaderDTO {
    private Long userId;
    private String deptName;
}
