package com.ideality.coreflow.attachment.query.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ideality.coreflow.attachment.query.dto.*;
import com.ideality.coreflow.infra.s3.S3Service;
import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.query.mapper.AttachmentMapper;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;
import com.ideality.coreflow.project.query.dto.CompletedTaskDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentQueryService {

	private final AttachmentMapper attachmentMapper;
	private final S3Service s3Service;

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


	public AttachmentDownloadDTO getAttachmentDownload(Long attachmentId) {
		return attachmentMapper.selectAttachmentInfoForDownload(attachmentId);
	}

	/* 설명. 댓글에 필요한 첨부 파일 가져오기 */
	public List<AttachmentForCommentDTO> selectAttachments(List<Long> commentIds) {
		List<AttachmentForCommentDTO> response = new ArrayList<>();

		for (Long commentId : commentIds) {
			AttachmentForCommentDTO attachment = attachmentMapper.selectAttachmentsForComments(commentId);
			response.add(attachment);
		}

		return response;
	}

	/* 설명. 태스크에 모든 댓글에 따라서 조회를 해오기 */
	public List<ResponseCommentAttachmentDTO> getAttachmentsByCommentId(List<Long> commentIds) {
		return attachmentMapper.selectAttachmentsByCommentId(commentIds);
	}


	/* 설명. 댓글 수정에서 사용 */
	public AttachmentForCommentDTO selectBycommentId(Long commentId) {
		return attachmentMapper.selectAttachmentsForComments(commentId);
	}
}
