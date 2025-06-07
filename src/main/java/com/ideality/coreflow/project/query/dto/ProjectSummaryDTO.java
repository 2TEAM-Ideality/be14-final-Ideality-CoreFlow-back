package com.ideality.coreflow.project.query.dto;


import com.ideality.coreflow.approval.domain.aggregate.Status;
import lombok.Getter;

@Getter
public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private Status status;
    private String startDate;
    private String endDate;
    private UserInfoDTO director;
    private Double progressRate;
    private Double passedRate;
    private Integer delayDays;

}
