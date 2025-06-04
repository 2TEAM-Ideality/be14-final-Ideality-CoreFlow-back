package com.ideality.coreflow.org.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_rank")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public void updateNameFrom(String newName) {
        this.name = newName;
    }
}
