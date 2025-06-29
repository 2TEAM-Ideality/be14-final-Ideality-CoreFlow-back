package com.ideality.coreflow.project.command.domain.repository;

import com.ideality.coreflow.project.command.domain.aggregate.Participant;
import com.ideality.coreflow.project.command.domain.aggregate.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findByUserIdAndTargetIdAndTargetType(Long targetId, Long taskId, TargetType targetType);
    Optional<Participant> findByTargetIdAndRoleId(Long targetId, Long roleId); // taskId와 roleId로 담당자 조회
    void deleteByTargetIdAndRoleId(Long targetId, Long roleId); // taskId와 roleId로 특정 참여자 삭제 taskId에 해당하는 모든 참여자 삭제

    boolean existsByTargetIdAndUserIdAndTargetType(long targetId, long userId, TargetType targetType);
}


