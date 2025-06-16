package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseParticipantUser {
    Long id;
    String name;
    String deptName;
    String jobRankName;
    String jobRoleName;
}
