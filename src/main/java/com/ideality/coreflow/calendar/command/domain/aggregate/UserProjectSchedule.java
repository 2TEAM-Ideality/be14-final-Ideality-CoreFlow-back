package com.ideality.coreflow.calendar.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_project_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProjectSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = false;
}
