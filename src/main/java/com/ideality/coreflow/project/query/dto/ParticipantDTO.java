package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDTO {
    private Long userId;
    private Long roleId;
    private String userName;


}