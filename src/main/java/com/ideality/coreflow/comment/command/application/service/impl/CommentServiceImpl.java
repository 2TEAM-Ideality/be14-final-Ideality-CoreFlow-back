package com.ideality.coreflow.comment.command.application.service.impl;

import com.ideality.coreflow.comment.command.application.service.CommentService;
import com.ideality.coreflow.comment.command.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commenntRepository;
}
