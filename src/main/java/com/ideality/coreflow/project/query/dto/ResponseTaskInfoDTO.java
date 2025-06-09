package com.ideality.coreflow.project.query.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseTaskInfoDTO {
    private SelectTaskDTO selectTask;
    private List<PrevTaskDTO> prevTasks;
    private List<NextTaskDTO> nextTasks;
}
