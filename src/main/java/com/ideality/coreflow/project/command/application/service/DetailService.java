package com.ideality.coreflow.project.command.application.service;

import com.ideality.coreflow.project.command.application.dto.RequestDetailDTO;
import com.ideality.coreflow.project.command.application.dto.RequestTaskDTO;

import java.util.List;

public interface DetailService {
    Long createDetail(RequestDetailDTO detailDTO);

    void validateSource(List<Long> source);

    void validateTarget(List<Long> target);

    Long updateDetail(Long detailId, RequestDetailDTO requestDetailDTO);

    void startDetail(Long workId);

    void completeDetail(Long workId);

    void deleteDetail(Long workId);
}
