package com.ideality.coreflow.project.command.application.dto;

import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ParticipantDTO {
    private Long targetId;   // targetId
    private Long userId;
    private TargetType targetType;
    private Long roleId;
}
