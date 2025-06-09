package com.ideality.coreflow.template.query.dto;

import com.ideality.coreflow.project.query.dto.TaskDeptDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateNodeDataDTO {
    private String label; 			        // 태스크명
    private String description; 	        // 설명
    private List<TaskDeptDTO> deptList; 	// 참여 부서 목록
    private int slackTime; 				    // 슬랙 타임
    private Integer duration;               // 태스크 소요일

}
