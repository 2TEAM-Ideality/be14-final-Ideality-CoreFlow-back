package com.ideality.coreflow.admin.command.application.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class RequestUserUpdateByAdmin {
    String name;
    String email;
    LocalDate birth;
    LocalDate hireDate;
    Boolean isResign;
    LocalDate resignDate;
    MultipartFile profileImage;
    String deptName;
    String jobRankName;
    String jobRoleName;

    Boolean isCreation;
}
