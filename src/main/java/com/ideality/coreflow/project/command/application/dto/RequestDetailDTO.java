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
    private Long parentTaskId; // 태스크 ID //

    private String name;
    private String description;
    private Long deptId; //dept부서명을 받아오는데,,,실제로는 id를 넘겨줘야..? >> 그럼 부서일정 목록 조회를 바꿔야하나?
    private LocalDate startBase;
    private LocalDate endBase;

    //클라이언트에서 넘겨줄 때 이름이 아니라 id로 넘겨줘야함... >>// 그러면 내 세부일정목록 조회를 id랑 받아오도록 좀 바꿔야하나?
    private Long predecessorTaskId;  // 선행 일정 ID -> relation 에 저장
    private Long successorTaskId; //

    private Long assigneeId;  // 책임자 이름 -> 얘네는 아이디랑 같이 받아옴!
    private List<Long> participantIds;  // 참여자 이름 리스트
}
