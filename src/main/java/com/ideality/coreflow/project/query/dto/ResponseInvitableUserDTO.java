package com.ideality.coreflow.project.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseInvitableUserDTO {
    private Long userId;
    private String name;
    private String jobRank;
    private String deptName;
    private boolean isParticipation;
}
