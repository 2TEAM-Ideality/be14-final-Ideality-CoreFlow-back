package com.ideality.coreflow.user.query.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AllUserDTO {
    private Long id;
    private String name;
    private String deptName;
    private String profileImage;
    private String jobRank;
}
