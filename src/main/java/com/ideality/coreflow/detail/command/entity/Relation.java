package com.ideality.coreflow.detail.command.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "relation")
@Getter
@Setter
@NoArgsConstructor
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prev_work_id")
    private Work prevWork;

    @ManyToOne
    @JoinColumn(name = "next_work_id")
    private Work nextWork;
}
