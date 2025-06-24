package com.ideality.coreflow.project.query.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TaskSummaryResponse {
    int taskCount;
    int detailCount;
    private List<WorkSummaryDTO> tasks;
    private List<WorkSummaryDTO> subtasks;
}
