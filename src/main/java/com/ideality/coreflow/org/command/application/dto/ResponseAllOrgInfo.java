package com.ideality.coreflow.org.command.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseAllOrgInfo {
    List<ResponseDept> deptList;
    List<ResponseJobRank> jobRankList;
    List<ResponseJobRole> jobRoleList;
}
