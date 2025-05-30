package com.ideality.coreflow.project.command.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RequestTaskDTO {
    /* 설명. 사용자는 편하게 flat 구조로 입력 값을 전부 받는다. */
    private String taskName;
    private String taskDescription;
    private LocalDate startBase;
    private LocalDate endBase;
    private Long projectId;
    /* 설명. 여기부터 담당 부서 데이터 */
    private String deptName;
    /* 설명. 여기부터 작업 간 관계 */
    private Long prevWorkId;
    private Long nextWorkId;
}