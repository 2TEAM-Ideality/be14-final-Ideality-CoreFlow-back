package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserInfoDTO {
    private Long userId;
    private String name;
    private String deptName;
    private String jobRoleName;
    private String profileImage;
    private String roleId;
}
