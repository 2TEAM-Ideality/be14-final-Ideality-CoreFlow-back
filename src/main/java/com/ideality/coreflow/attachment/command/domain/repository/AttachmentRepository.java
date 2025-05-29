package com.ideality.coreflow.attachment.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ideality.coreflow.attachment.command.domain.aggregate.Attachment;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
