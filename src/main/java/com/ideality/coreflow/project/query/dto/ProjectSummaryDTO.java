package com.ideality.coreflow.project.query.dto;


import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalStatus;
import lombok.Getter;

@Getter
public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private ApprovalStatus status;
    private String startDate;
    private String endDate;
    private DirectorSummaryDTO director;
    private Double progressRate;
    private Double passedRate;
    private Integer delayDays;

}
