package com.ideality.coreflow.project.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class nextTaskDTO {
    private Long relationId;
    private Long nextWorkId;
    private String nextWorkName;
}
