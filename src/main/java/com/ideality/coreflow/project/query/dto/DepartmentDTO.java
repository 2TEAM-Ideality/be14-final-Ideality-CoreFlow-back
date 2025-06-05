package com.ideality.coreflow.project.query.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DepartmentDTO {
    private Long deptId;   // 부서 ID
    private String deptName;   // 부서명
}
