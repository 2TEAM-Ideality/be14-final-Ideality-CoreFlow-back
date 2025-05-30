package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}