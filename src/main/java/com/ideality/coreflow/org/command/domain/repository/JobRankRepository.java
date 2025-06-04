package com.ideality.coreflow.org.command.domain.repository;

import com.ideality.coreflow.org.command.domain.aggregate.JobRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRankRepository extends JpaRepository<JobRank, Integer> {
    void deleteById(Long id);

    Optional<JobRank> findByName(String prevJobRankName);
}
