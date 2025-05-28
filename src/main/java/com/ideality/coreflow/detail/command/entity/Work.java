package com.ideality.coreflow.detail.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // 작업명
    private String description;  // 작업 설명

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.DATE)
    private Date startBase;

    @Temporal(TemporalType.DATE)
    private Date endBase;

    @Temporal(TemporalType.DATE)
    private Date startExpect;

    @Temporal(TemporalType.DATE)
    private Date endExpect;

    @Temporal(TemporalType.DATE)
    private Date startReal;

    @Temporal(TemporalType.DATE)
    private Date endReal;

    private Double progressRate;
    private Double passedRate;
    private Integer delayDays;

    private String status;

    @ManyToOne
    @JoinColumn(name = "dept_id")
    private Dept dept;  // 부서

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Work parentTask;  // 부모 작업 (세부 일정 구분)

    @OneToMany(mappedBy = "parentTask")
    private List<Work> subTasks;  // 하위 작업들 (세부 일정)

    @OneToMany(mappedBy = "work")
    private List<WorkDept> workDepts;  // 작업에 참여하는 부서

    @OneToMany(mappedBy = "work")
    private List<Participant> participants;  // 작업에 참여하는 인원

    // Getter, Setter
}

