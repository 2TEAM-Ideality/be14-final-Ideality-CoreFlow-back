package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkDetailDTO {
    private Long workId;
    private String taskName;
    private String taskDescription;
    private Date startExpect;
    private Date endExpect;
    private Date startReal;
    private Date endReal;
    private Double progressRate;
    private Integer delayDays;
    private String taskStatus;
    private Long deptId;
    private String deptName;

    private String prevWorkIdsString;  // 선행 일정 String
    private String nextWorkIdsString;  // 후행 일정 String

    private List<Long> prevWorkIds;  // 선행 일정 ID 리스트
    private List<Long> nextWorkIds;  // 후행 일정 ID 리스트
    private List<ParticipantDTO> participants;  // 참여자 목록

    // prevWorkIdsString을 List<Long>으로 변환
    public void setPrevWorkIds() {
        if (prevWorkIdsString != null && !prevWorkIdsString.isEmpty()) {
            this.prevWorkIds = Arrays.stream(prevWorkIdsString.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } else {
            this.prevWorkIds = new ArrayList<>();  // 빈 리스트로 처리
        }
    }

    // nextWorkIdsString을 List<Long>으로 변환
    public void setNextWorkIds() {
        if (nextWorkIdsString != null && !nextWorkIdsString.isEmpty()) {
            this.nextWorkIds = Arrays.stream(nextWorkIdsString.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } else {
            this.nextWorkIds = new ArrayList<>();  // 빈 리스트로 처리
        }
    }

    // setParticipants 메서드 추가: 참여자 정보 처리
    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants != null ? participants : new ArrayList<>();
    }
}
