package com.ideality.coreflow.project.command.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RequestDetailDTO {
    private Long projectId; // 프로젝트 ID
    private Long parentTaskId; // 태스크 ID

    private String name;
    private String description;

    // 설명. 프론트에서 부서명을 선택하면, 실제로는 부서 id가 선택됨.
    private Long deptId;
    private LocalDate startBase;
    private LocalDate endBase;

    //설명. 프론트에서 보여줄 때 세부일정명만 보여주지만, 실제로 선택시에는 해당 일정명의 id가 저장됨.
    private List<Long> Source;
    private List<Long> Target;

    private Long assigneeId;  // 이름과 아이디랑 같이 받아 오긴함.
    private List<Long> participantIds;
}
