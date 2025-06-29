package com.ideality.coreflow.project.command.application.dto;

import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestDeleteParticipant {
    Long userId;
    Long targetId; // taskId or projectId
    TargetType targetType;
}
