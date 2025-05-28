package com.ideality.coreflow.detail.command.repository;

import com.ideality.coreflow.detail.command.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
