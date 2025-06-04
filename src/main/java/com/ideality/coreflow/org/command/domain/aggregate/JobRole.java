package com.ideality.coreflow.org.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_role")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public void updateNameFrom(String newName) {
        this.name = newName;
    }
}
