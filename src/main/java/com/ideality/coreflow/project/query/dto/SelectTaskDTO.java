package com.ideality.coreflow.project.query.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SelectTaskDTO {

    /* 설명. list로 조회해오는 데이터 빼고 조회
     *  Task 정보 데이터 빼고 조회 -> 부서, 이전 태스크 or 이후 태스크
    * */
    private Long taskId;
    private String taskName;
    private String description;
    private LocalDate startBaseLine;
    private LocalDate endBaseLine;
    private LocalDate expectStartDate;
    private LocalDate expectEndDate;
    private Double progressRate;
    private Double passedRate;
    private int delayDay;
    // 여기서부터 헤더에 표시할 프로젝트 데이터 정보(props로 전해줄거면 추후에 삭제)
    private String projectName;
    private Long projectId;
}
