package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DetailDTO {
    private Long workId;
    private String taskName;
    private Date endExpect;
    private Integer delayDays;
    private Double progressRate;

    private Long deptId;
    private String deptName;
    private String status;
}
