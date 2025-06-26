package com.ideality.coreflow.user.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MentionSearchParamDTO {
    private Long projectId;
    private String keyword;
    private String deptName;
    private String name;
}
