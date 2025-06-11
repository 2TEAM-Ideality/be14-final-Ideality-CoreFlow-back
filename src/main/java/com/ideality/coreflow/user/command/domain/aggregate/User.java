package com.ideality.coreflow.user.command.domain.aggregate;

import com.ideality.coreflow.user.command.application.dto.UserInfoDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_num", nullable = false)
    private String employeeNum;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "is_resign", nullable = false)
    private Boolean isResign = false;

    @Column(name = "resign_date")
    private LocalDate resignDate;

    @Column(name = "profile_image", columnDefinition = "TEXT")
    private String profileImage;

    @Column(name = "dept_name", nullable = false)
    private String deptName;

    @Column(name = "job_rank_name", nullable = false)
    private String jobRankName;

    @Column(name = "job_role_name", nullable = false)
    private String jobRoleName = "사원";

    public void updateFrom(UserInfoDTO dto) {
        if (dto.getPassword() != null) this.password = dto.getPassword();
        if (dto.getName() != null) this.name = dto.getName();
        if (dto.getEmail() != null) this.email = dto.getEmail();
        if (dto.getBirth() != null) this.birth = dto.getBirth();
        if (dto.getHireDate() != null) this.hireDate = dto.getHireDate();
        if (dto.getIsResign() != null) this.isResign = dto.getIsResign();
        if (dto.getResignDate() != null) this.resignDate = dto.getResignDate();
        if (dto.getProfileImage() != null) this.profileImage = dto.getProfileImage();
        if (dto.getDeptName() != null) this.deptName = dto.getDeptName();
        if (dto.getJobRankName() != null) this.jobRankName = dto.getJobRankName();
        if (dto.getJobRoleName() != null) this.jobRoleName = dto.getJobRoleName();
    }

    public void updateOrg(OrgType type, String newName) {
        switch (type) {
            case DEPT -> this.deptName = newName;
            case JOB_RANK -> this.jobRankName = newName;
            case JOB_ROLE -> this.jobRoleName = newName;
        }
    }
}
