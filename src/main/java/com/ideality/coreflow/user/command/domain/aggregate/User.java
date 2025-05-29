package com.ideality.coreflow.user.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
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
}
