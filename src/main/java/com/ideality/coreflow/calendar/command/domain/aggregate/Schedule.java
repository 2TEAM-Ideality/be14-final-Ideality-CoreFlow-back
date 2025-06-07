package com.ideality.coreflow.calendar.command.domain.aggregate;

import com.ideality.coreflow.calendar.command.application.dto.UpdateScheduleDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String content;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "is_repeat", nullable = false)
    private Boolean isRepeat = false;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType = EventType.PERSONAL;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "dept_id")
    private Long deptId;

    public void updateSchedule(UpdateScheduleDTO requestDTO) {
        this.name = requestDTO.getName();
        this.content = requestDTO.getContent();
        this.startAt = requestDTO.getStartDate();
        this.endAt = requestDTO.getEndDate();
        this.isRepeat = requestDTO.getIsRepeat();
        this.eventType = EventType.valueOf(requestDTO.getEventType());
    }

    public void deleteSchedule() {
        this.isDeleted = true;
    }
}
