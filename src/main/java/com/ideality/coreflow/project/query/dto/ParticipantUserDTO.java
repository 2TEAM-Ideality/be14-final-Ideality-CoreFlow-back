package com.ideality.coreflow.project.query.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantUserDTO {
    Long userId;
    String name;
    Long projectId;
    String deptName;
    String jobRankName;
    String jobRoleName;
}
