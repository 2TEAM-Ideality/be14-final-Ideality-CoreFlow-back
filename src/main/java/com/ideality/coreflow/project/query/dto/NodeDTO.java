package com.ideality.coreflow.project.query.dto;

import com.ideality.coreflow.template.query.dto.DeptDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@ToString
public class NodeDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startBase;
    private LocalDate endBase;
    private LocalDate startExpect;
    private LocalDate endExpect;
    private LocalDate startReal;
    private LocalDate endReal;
    private Integer progressRate;
    private Integer passedRate;
    private Integer delayDays;
    private String status;
    private Boolean warning;
    private List<DeptDTO> deptList;
}
