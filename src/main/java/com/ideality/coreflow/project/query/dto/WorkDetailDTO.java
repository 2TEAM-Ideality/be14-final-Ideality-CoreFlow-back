package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkDetailDTO {
    private Long workId;
    private String taskName;
    private String taskDescription;
    private Date startExpect;
    private Date endExpect;
    private Date startReal;
    private Date endReal;
    private Double progressRate;
    private Integer delayDays;
    private String taskStatus;
    private Long deptId;
    private String deptName;
    private List<Long> prevWorkIds;  // 선행 일정 ID들
    private List<Long> nextWorkIds;  // 후행 일정 ID들
    private List<ParticipantDTO> participants;  // 참여자 목록
}