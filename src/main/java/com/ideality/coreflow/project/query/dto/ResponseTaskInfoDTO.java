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
    private List<String> deptNames;
    private List<PrevTaskDTO> prevTasks;
    private List<NextTaskDTO> nextTasks;

    public void setNearDueSubtaskCount(Long nearDueSubtaskCount) {
        this.selectTask.setNearDueSubtasks(nearDueSubtaskCount);
    }
}
