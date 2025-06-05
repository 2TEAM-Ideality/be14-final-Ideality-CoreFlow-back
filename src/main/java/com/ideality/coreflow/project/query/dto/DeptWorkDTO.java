package com.ideality.coreflow.project.query.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DeptWorkDTO {

	private Long id;
	private String taskName;
	private String taskDescription;

	private int slackTime;

	private Date startExpect;
	private Date endExpect;
	private Date startReal;
	private Date endReal;

	private Long deptId;
	private String deptName;

	private List<ParticipantDTO> participants;  // 참여자 목록

	private Long projectId;						// 프로젝트 아이디

}