package com.ideality.coreflow.project.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class WorkDeptDTO {
    private Long taskId;
    private Long deptId;
    private String deptName;
}
