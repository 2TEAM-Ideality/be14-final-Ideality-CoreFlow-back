package com.ideality.coreflow.project.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ResponseParticipantDTO {
    private Long userId;
    private String profileImage;
    private String name;
    private String deptName;
    private String jobRank;
    private Long roleId;
}
