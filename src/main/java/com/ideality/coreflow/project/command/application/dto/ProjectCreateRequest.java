package com.ideality.coreflow.project.command.application.dto;

import com.ideality.coreflow.template.query.dto.TemplateDataDTO;
import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjectCreateRequest {
    /* 설명: 템플릿을 사용하지 않는 경우에 사용하는 DTO */

    private String name;
    private String description;

    private LocalDate startBase;
    private LocalDate endBase;

    private LocalDate endExpect;

    private List<Long> leaderIds;
    private Long directorId;

    private Long templateId;
    private TemplateDataDTO templateData;
}