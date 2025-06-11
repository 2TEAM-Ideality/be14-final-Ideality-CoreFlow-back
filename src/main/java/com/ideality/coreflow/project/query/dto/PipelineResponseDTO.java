package com.ideality.coreflow.project.query.dto;

import com.ideality.coreflow.template.query.dto.EdgeDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PipelineResponseDTO {
    private String name;
    private String description;
    private LocalDate startBase;
    private LocalDate endBase;
    private LocalDate startExpect;
    private LocalDate endExpect;
    private LocalDate startReal;
    private LocalDate endReal;
    private Double progressRate;
    private Double passedRate;
    private Integer delayDays;
    private String status;
    private List<NodeDTO> nodeList;
    private List<EdgeDTO> edgeList;
}
