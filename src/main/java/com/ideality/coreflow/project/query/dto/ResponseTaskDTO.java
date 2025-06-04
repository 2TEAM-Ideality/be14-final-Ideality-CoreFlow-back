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
public class ResponseTaskDTO {

    private Long id;
    private String label;
    private String description;
    private List<TaskDeptDTO> depts;
    private int slackTime;
    private LocalDate startBaseLine;
    private LocalDate endBaseLine;
}
