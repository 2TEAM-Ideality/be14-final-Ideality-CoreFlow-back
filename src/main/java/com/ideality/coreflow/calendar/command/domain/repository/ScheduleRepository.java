package com.ideality.coreflow.calendar.command.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ideality.coreflow.calendar.command.domain.aggregate.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
