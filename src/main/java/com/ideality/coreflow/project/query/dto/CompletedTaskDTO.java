package com.ideality.coreflow.project.query.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class CompletedTaskDTO {
	
	// 일단, 프로젝트 분석 리포트용 - 완료된 태스크 정보
	private Long projectId;
	private Long taskId;
	private String taskName;
	private LocalDateTime createdAt;
	private String description;
	private String status;
	private LocalDate startBase;
	private LocalDate endBase;
	private LocalDate startExpect;
	private LocalDate endExpect;

	private LocalDate startReal;
	private LocalDate endReal;

	private Double progressRate; // 진척률
	private Double passedRate;   // 경과율
	private Integer delayDays;
	private Integer slackTime;
}
