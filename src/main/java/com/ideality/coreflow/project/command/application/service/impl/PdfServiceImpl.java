package com.ideality.coreflow.project.command.application.service.impl;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.ideality.coreflow.project.command.application.service.PdfService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

	private final TemplateEngine templateEngine;




}
