package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSummaryDTO {
    private Long workId;
    private Long parentTaskId;
    private String name;
}
