package com.ideality.coreflow.calendar.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dept")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dept {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "dept_code", nullable = false, length = 10)
    private String deptCode;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_dept_id")
    private Dept parentDept;
}
