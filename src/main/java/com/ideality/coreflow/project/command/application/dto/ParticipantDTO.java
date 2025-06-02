package com.ideality.coreflow.project.command.application.dto;

import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ParticipantDTO {
    private Long taskId;
    private Long userId;
    private TargetType targetType;
    private Long roleId;
}
