package com.ideality.coreflow.holiday.command.domain.repository;

import com.ideality.coreflow.holiday.command.domain.aggregate.Holiday;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByDate(LocalDate date);
}
