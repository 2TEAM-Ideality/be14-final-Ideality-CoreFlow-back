package com.ideality.coreflow.approval.query.dto;

import com.ideality.coreflow.approval.command.domain.aggregate.ApprovalType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectApprovalDTO {
    long id;                    // 결재 id
    String taskName;            // 태스크명
    LocalDateTime createdDate;  // 생성일
    String approverName;        // 결재자 명
    String approverJobRank;     // 결재자 직위
    String approvalJobRole;     // 결재자 직책

    ApprovalType approvalType;  // 결재 타입 (산출물/지연 사유서)

    String delayReason;         // 지연 사유
    Integer delayDays;          // 지연 일

    String url;                 // 파일 링크

    // 작성 날짜만 반환 (시간x)
    public LocalDate getCreatedDate() {
        return createdDate.toLocalDate();
    }
}
