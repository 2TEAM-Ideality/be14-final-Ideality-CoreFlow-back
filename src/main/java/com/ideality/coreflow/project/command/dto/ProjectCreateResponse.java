package com.ideality.coreflow.project.command.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectCreateResponse {
    private Long projectId;
}
