package com.ideality.coreflow.project.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PrevTaskDTO {
    private Long relationId;
    private Long prevWorkId;
    private String prevWorkName;
}
