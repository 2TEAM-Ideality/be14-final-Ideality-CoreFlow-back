package com.ideality.coreflow.detail.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // 사용자 이름
    private String email; // 이메일

    // 부서 정보 (현재 부서 정보를 가져오는 용도로 활용)
    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Dept dept;

}
