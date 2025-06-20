package com.ideality.coreflow.project.query.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ParticipantTeamDTO {
    private Long deptId;
    private String deptName;
    private UserInfoDTO teamLeader;
    private List<UserInfoDTO> teamMembers;
}
