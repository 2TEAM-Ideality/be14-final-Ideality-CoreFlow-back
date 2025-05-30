package com.ideality.coreflow.project.command.application.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TaskParticipantDTO {
    private Long taskId;
    private Long userId;
    private Long roleId;
}
