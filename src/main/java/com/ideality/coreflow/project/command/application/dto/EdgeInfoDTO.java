package com.ideality.coreflow.project.command.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EdgeInfoDTO {
    Long id;
    Long sourceId;
    Long targetId;
}
