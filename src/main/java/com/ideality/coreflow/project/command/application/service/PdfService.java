package com.ideality.coreflow.project.command.application.service;

import java.util.List;

import com.ideality.coreflow.approval.query.dto.ProjectApprovalDTO;
import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;

import jakarta.servlet.http.HttpServletResponse;

public interface PdfService {
	void createReportPdf(HttpServletResponse response, ProjectDetailResponseDTO projectDetail, List<CompletedTaskDTO> completedTaskList,
		List<ProjectApprovalDTO> delayList, List<ReportAttachmentDTO> attachmentList);

}
