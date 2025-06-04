package com.ideality.coreflow.org.command.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseJobRole {
    long id;
    String name;
}
