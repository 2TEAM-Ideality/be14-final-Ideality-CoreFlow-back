package com.ideality.coreflow.attachment.command.application.service;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.command.domain.repository.AttachmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {

	private final AttachmentRepository attachmentRepository;

}
