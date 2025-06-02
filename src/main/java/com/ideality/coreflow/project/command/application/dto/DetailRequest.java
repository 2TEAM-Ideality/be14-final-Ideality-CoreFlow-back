package com.ideality.coreflow.project.command.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class DetailRequest {

    private Long projectId; // 프로젝트 ID
    private Long parentTaskId; // 태스크 ID //

    private String name;
    private String description;
    private Long deptId;
    private LocalDate startBase;
    private LocalDate endBase;

    private Long predecessorTaskId;  // 선행 일정 ID -> relation 에 저장
    private Long successorTaskId;    // 후행 일정 ID

    private Long assigneeId;  // 책임자 ID ->
    private List<Long> participantIds;  // 참여자 ID 리스트

}
