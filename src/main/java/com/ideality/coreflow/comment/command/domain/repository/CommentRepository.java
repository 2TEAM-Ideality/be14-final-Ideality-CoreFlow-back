package com.ideality.coreflow.comment.command.domain.repository;

import com.ideality.coreflow.comment.command.domain.aggregate.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
