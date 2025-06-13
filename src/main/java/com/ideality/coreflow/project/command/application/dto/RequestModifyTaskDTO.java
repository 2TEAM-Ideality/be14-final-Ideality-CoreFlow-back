package com.ideality.coreflow.project.command.application.dto;

import com.ideality.coreflow.project.query.dto.NextTaskDTO;
import com.ideality.coreflow.project.query.dto.PrevTaskDTO;
import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RequestModifyTaskDTO {

    private List<TaskDeptDTO> deptLists;
    private String description;
    private List<PrevTaskDTO> prevTaskList;
    private List<NextTaskDTO> nextTaskList;
    private LocalDate startExpect;
    private LocalDate endExpect;
}
