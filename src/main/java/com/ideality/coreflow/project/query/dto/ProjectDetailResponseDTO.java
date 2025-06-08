package com.ideality.coreflow.project.query.dto;

import com.ideality.coreflow.project.command.domain.aggregate.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
//@NoArgsConstructor
@RequiredArgsConstructor
@Builder
public class ProjectDetailResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate createdDate;
    private LocalDate startBase;
    private LocalDate endBase;
    private LocalDate startExpect;
    private LocalDate endExpect;
    private LocalDate startReal;
    private LocalDate endReal;
    private Double progressRate;
    private Double passedRate;
    private Integer delayDays;
    private Status status;

    private UserInfoDTO director;
    private List<UserInfoDTO> leaders;
}
