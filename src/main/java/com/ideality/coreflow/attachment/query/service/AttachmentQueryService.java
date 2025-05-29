package com.ideality.coreflow.attachment.query.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.dto.ResponseAttachmentDTO;
import com.ideality.coreflow.attachment.query.mapper.AttachmentMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentQueryService {

	AttachmentMapper attachmentMapper;

	// TARGET TYPE, TARGET ID 로 URL 가져오기
	public String getUrl(Long templateId, FileTargetType targetType) {
		Map<String, Object> paramMap = Map.of(
			"targetId", templateId,
			"targetType", targetType.name() // ENUM을 문자열로 넘김
		);
		ResponseAttachmentDTO response = attachmentMapper.selectUrl(paramMap);

		return response.getUrl();		// URL 정보만 넘겨주기
	}
}
