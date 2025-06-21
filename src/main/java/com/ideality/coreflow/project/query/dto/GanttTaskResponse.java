package com.ideality.coreflow.project.query.dto;

import java.time.LocalDate;
import java.util.List;

import com.ideality.coreflow.project.command.domain.aggregate.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
@Setter
public class GanttTaskResponse {
    private Long taskId;
    private String taskName;
    private LocalDate startBase;
    private LocalDate endBase;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double progress;
    private Status status;
    private Integer delayDays;
    private Boolean isChild;
    private List<Long> predecessor;
    private List<GanttTaskResponse> subTasks;
    private List<Long> resources;
}
