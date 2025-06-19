package com.ideality.coreflow.project.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseTaskList {
    Long id;
    List<TaskPreviewDTO> tasks;
}
