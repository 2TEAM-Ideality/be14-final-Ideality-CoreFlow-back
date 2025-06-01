package com.ideality.coreflow.project.command.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCreateRequest {
    /* 설명: 템플릿을 사용하지 않는 경우에 사용하는 DTO */

    private String name;
    private String description;

    private LocalDate startBase;
    private LocalDate endBase;
}
