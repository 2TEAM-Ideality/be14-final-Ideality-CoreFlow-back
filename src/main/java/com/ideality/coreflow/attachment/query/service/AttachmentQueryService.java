package com.ideality.coreflow.attachment.query.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.dto.ReportAttachmentDTO;
import com.ideality.coreflow.attachment.query.dto.ResponseAttachmentDTO;
import com.ideality.coreflow.attachment.query.mapper.AttachmentMapper;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentQueryService {

	private final AttachmentMapper attachmentMapper;

	// TARGET TYPE, TARGET ID 로 URL 가져오기
	public String getUrl(Long templateId, FileTargetType targetType) {
		Map<String, Object> paramMap = Map.of(
			"targetId", templateId,
			"targetType", targetType.name()
		);

		ResponseAttachmentDTO response = attachmentMapper.selectUrl(paramMap);

		if (response == null) {
			log.warn("첨부파일 조회 실패 - targetId: {}, targetType: {}", templateId, targetType.name());
			throw new BaseException(ErrorCode.ATTCHMENT_NOT_FOUND);
		}

		return response.getUrl();
	}

	// 프로젝트 산출물 내역 가져오기
	public List<ReportAttachmentDTO> getAttachmentsByProjectId(Long projectId) {
		List<ReportAttachmentDTO> response = attachmentMapper.selectAttachmentsByProjectId(projectId);

		if (response == null) {
			log.warn("첨부파일 조회 실패");
			throw new BaseException(ErrorCode.ATTCHMENT_NOT_FOUND);
		}
		return response;
	}
}
