package com.ideality.coreflow.project.command.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DetailRequest {

    private String name;
    private String description;
    private Date startBase;
    private Date endBase;
    private Long deptId;
    private Long userId;

}
