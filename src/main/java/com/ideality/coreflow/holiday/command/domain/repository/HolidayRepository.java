package com.ideality.coreflow.holiday.command.domain.repository;

import com.ideality.coreflow.holiday.command.domain.aggregate.Holiday;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsByDate(LocalDate date);

    List<Holiday> findByDateBetween(LocalDate of, LocalDate of1);
}
