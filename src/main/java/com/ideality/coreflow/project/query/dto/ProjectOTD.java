package com.ideality.coreflow.project.query.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectOTD {
	private Long projectId;
	private String projectName;
	private double otdRate;
	private	int completedOnTime;
	private int notCompletedOnTime;
	private int totalTask;
}