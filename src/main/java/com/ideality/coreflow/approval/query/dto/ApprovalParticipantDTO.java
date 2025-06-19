package com.ideality.coreflow.approval.query.dto;

import lombok.Getter;

@Getter
public class ApprovalParticipantDTO {
    long id;
    long participantId;
    String participantName;
    String participantDeptName;
    String participantRole;
}
