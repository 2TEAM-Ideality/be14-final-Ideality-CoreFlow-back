package com.ideality.coreflow.project.query.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.ideality.coreflow.template.query.dto.DeptDTO;

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
