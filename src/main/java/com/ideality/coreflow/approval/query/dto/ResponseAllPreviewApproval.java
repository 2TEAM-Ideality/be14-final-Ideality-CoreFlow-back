package com.ideality.coreflow.approval.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseAllPreviewApproval {
    List<ResponsePreviewApproval> receivedApproval;
    List<ResponsePreviewApproval> sentApproval;
}
