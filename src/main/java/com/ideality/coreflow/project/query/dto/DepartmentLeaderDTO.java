package com.ideality.coreflow.project.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DepartmentLeaderDTO {
    private Long userId;
    private Long participantId;
    private String name;
    private String deptName;
    private String profileImage;
    private String jobRank;
}
