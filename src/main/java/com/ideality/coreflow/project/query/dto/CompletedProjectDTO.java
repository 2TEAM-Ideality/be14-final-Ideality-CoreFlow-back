package com.ideality.coreflow.project.query.dto;

import java.util.List;

import com.ideality.coreflow.project.command.domain.aggregate.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompletedProjectDTO {
	private Long id;
	private String name;
	private Status status;
	private String startDate;
	private String endDate;
	private Double progressRate;
	private Double passedRate;
	private Integer delayDays;
	private Integer duration;
	private Integer taskCount;
	private List<DepartmentDTO> deptList;
}
