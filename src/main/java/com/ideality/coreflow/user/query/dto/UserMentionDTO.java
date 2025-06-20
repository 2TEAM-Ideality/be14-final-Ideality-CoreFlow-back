package com.ideality.coreflow.user.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserMentionDTO {
    private Long id;
    private String name;
    private String deptName;
}
