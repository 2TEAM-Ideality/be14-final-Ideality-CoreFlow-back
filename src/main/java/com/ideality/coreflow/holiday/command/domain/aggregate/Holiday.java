package com.ideality.coreflow.holiday.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HolidayType type;

    @Column(name = "is_repeat", nullable = false)
    @Enumerated(EnumType.STRING)
    private RepeatType isRepeat = RepeatType.ONCE;
}
