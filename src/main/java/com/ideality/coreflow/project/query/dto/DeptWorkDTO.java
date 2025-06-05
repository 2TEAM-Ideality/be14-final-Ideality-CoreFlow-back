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
	// 그냥 다 보내주고 null 이면 알아서 거기서 처리하도록 하자.

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

	// setParticipants 메서드 추가: 참여자 정보 처리
	public void setParticipants(List<ParticipantDTO> participants) {
		this.participants = participants != null ? participants : new ArrayList<>();
	}

}