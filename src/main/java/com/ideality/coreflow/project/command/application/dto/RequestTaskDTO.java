package com.ideality.coreflow.project.command.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RequestTaskDTO {
    /* 설명. 사용자는 편하게 flat 구조로 입력 값을 전부 받는다. */
    private String label;
    private String description;
    private LocalDate startBaseLine;
    private LocalDate endBaseLine;
    private Long projectId;
    /* 설명. 여기부터 담당 부서 데이터 */
    private List<Long> deptList;
    /* 설명. 여기부터 작업 간 관계 */
    private Long source;
    private Long target;

}