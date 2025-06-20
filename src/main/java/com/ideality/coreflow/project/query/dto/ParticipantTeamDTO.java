package com.ideality.coreflow.project.query.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ParticipantTeamDTO {
    private String deptName;
    private UserInfoDTO teamLeader;
    private List<UserInfoDTO> teamMembers;
}
