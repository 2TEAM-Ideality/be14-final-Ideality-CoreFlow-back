package com.ideality.coreflow.template.command.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideality.coreflow.attachment.command.application.service.AttachmentService;
import com.ideality.coreflow.infra.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateCommandFacadeService {

	private final AttachmentService attachmentService;
	private final S3Service s3Service;
	private final ObjectMapper objectMapper = new ObjectMapper();




}
