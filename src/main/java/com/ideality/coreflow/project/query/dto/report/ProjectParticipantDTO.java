package com.ideality.coreflow.project.query.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ProjectParticipantDTO {
	private Long projectId;
	private Long userId;
	private String userName;
	private String deptName;
	private String jobRankName;
	private String jobRoleName;


}
