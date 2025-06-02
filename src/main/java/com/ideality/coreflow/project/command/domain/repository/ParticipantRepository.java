package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByIdAndUserId(Long id, Long userId);

    Optional<Object> findByTargetIdAndUserId(Long targetId, Long userId);
}
