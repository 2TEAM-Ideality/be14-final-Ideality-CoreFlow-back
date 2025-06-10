package com.ideality.coreflow.project.command.application.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.project.command.application.service.PdfService;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

	private final TemplateEngine templateEngine;

	@Override
	public void createReportPdf(
		ProjectDetailResponseDTO projectDetail,
		List<CompletedTaskDTO> completedTaskList,
		List<ReportAttachmentDTO> attachmentList) {

		// 가져온 정보로 프로젝트 분석 리포트 PDF 만들기



	}
}
