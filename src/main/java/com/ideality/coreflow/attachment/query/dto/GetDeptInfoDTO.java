package com.ideality.coreflow.attachment.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GetDeptInfoDTO {
    private Long taskId;
    private String taskName;
    private String deptName;
}
