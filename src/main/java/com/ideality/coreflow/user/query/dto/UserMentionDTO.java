package com.ideality.coreflow.user.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserMentionDTO {
    private String name;
    private String jobRank;
    private String deptName;
}
