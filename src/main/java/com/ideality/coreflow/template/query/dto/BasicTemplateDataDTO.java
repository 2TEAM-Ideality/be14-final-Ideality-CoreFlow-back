package com.ideality.coreflow.template.query.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicTemplateDataDTO {
    private List<TemplateNodeDTO> nodeList;
    private List<EdgeDTO> edgeList;
}
