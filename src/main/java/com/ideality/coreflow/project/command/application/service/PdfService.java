package com.ideality.coreflow.project.command.application.service;

import java.util.List;

import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;
import com.ideality.coreflow.project.query.dto.ProjectDetailResponseDTO;

public interface PdfService {
	void createReportPdf(ProjectDetailResponseDTO projectDetail, List<CompletedTaskDTO> completedTaskList, List<ReportAttachmentDTO> attachmentList);

}
