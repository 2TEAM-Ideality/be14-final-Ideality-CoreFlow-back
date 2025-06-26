package com.ideality.coreflow.project.command.application.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RequestRelationUpdateDTO {
    /* 설명. 파이프라인에서 (관계만!) 수정할 때 DTO */
    private Long projectId;
    private Long taskId;
    private List<Long> source;
    private List<Long> target;
}
