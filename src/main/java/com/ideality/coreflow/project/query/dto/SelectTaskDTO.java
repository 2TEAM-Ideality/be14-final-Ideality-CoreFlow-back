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

    /* 설명. list로 조회해오는 데이터 빼고 조회 */
    private Long id;
    private String description;
    private LocalDate startBaseLine;
    private LocalDate endBaseLine;
    private LocalDate expectStartDate;
    private LocalDate expectEndDate;
    private Double progressRate;
    private Double passedRate;
    private int delayDay;
}
