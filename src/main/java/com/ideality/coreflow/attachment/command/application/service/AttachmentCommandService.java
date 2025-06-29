package com.ideality.coreflow.attachment.command.application.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ideality.coreflow.attachment.command.application.dto.AttachmentPreviewDTO;
import com.ideality.coreflow.attachment.command.application.dto.CreateAttachmentDTO;
import com.ideality.coreflow.attachment.command.application.dto.RegistAttachmentDTO;
import org.springframework.stereotype.Service;

import com.ideality.coreflow.attachment.command.domain.aggregate.Attachment;
import com.ideality.coreflow.attachment.command.domain.aggregate.FileTargetType;
import com.ideality.coreflow.attachment.command.domain.repository.AttachmentRepository;
import com.ideality.coreflow.common.exception.BaseException;
import com.ideality.coreflow.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentCommandService {

	private final AttachmentRepository attachmentRepository;

	// 첨부파일 생성
	@Transactional
	public void createAttachmentForTemplate(Long templateId, String fileName, String fileUrl, Long createdBy, String json) {
		Attachment attachment = Attachment.builder()
			.originName("template.json")
			.storedName(fileName)
			.url(fileUrl)
			.fileType("application/json")
			.size(String.valueOf(json.getBytes(StandardCharsets.UTF_8).length))
			.uploadAt(LocalDateTime.now())
			.uploaderId(createdBy)
			.targetType(FileTargetType.TEMPLATE)
			.targetId(templateId)
			.isDeleted(false)
			.build();
		attachmentRepository.save(attachment);
	}


	// OK. TargetType 받아서 업데이트하는 방법 <모든 파일 타입 공통으로 적용 가능>
	@Transactional
	public void updateAttachmentForTemplate(FileTargetType fileType, Long targetId, String fileName, String fileUrl, String size, Long updatedBy) {
		// 타겟 아이디, 타겟 타입을 조합해서 기존 첨부파일 찾기
		Attachment originAttachment = attachmentRepository.findByTargetIdAndTargetType(targetId, fileType)
			.orElseThrow(() -> new BaseException(ErrorCode.ATTCHMENT_NOT_FOUND));

		// 파일명, URL, 사이즈만 수정
		originAttachment.updateInfo(
			fileName,
			fileUrl,
			size,
			updatedBy
		);
	}

	// 첨부 파일 삭제 여부 변경
	public void deleteAttachment(Long targetId, FileTargetType fileType) {
		Attachment originAttachment = attachmentRepository.findByTargetIdAndTargetType(targetId, fileType)
			.orElseThrow(() -> new BaseException(ErrorCode.ATTCHMENT_NOT_FOUND));

		originAttachment.delete();

	}

	public void registAttachment(RegistAttachmentDTO approvalAttachment) {
		Attachment attachment = Attachment.builder()
				.originName(approvalAttachment.getOriginName())
				.storedName(approvalAttachment.getStoredName())
				.url(approvalAttachment.getUrl())
				.fileType(approvalAttachment.getFileType())
				.size(approvalAttachment.getSize())
				.uploadAt(LocalDateTime.now())
				.targetType(approvalAttachment.getTargetType())
				.targetId(approvalAttachment.getTargetId())
				.uploaderId(approvalAttachment.getUploaderId())
				.isDeleted(false)
				.build();
		attachmentRepository.save(attachment);
	}

	public AttachmentPreviewDTO getOriginName(long approvalId, FileTargetType fileTargetType) {
		return attachmentRepository.findByTargetIdAndTargetType(approvalId, fileTargetType)
				.map(attachment -> AttachmentPreviewDTO.builder()
						.originName(attachment.getOriginName())
						.url(attachment.getUrl())
						.build())
				.orElse(null);
	}

	public List<AttachmentPreviewDTO> getOriginNameList(long approvalId, FileTargetType fileTargetType) {
		List<Attachment> attachments = attachmentRepository.findListByTargetIdAndTargetType(approvalId, fileTargetType);

		if (attachments == null || attachments.isEmpty()) {
			return Collections.emptyList();
		}

		return attachments.stream()
				.map(attachment -> AttachmentPreviewDTO.builder()
						.originName(attachment.getOriginName())
						.url(attachment.getUrl())
						.build())
				.collect(Collectors.toList());
	}

	public Boolean findAttachmentByTargetId(Long templateId) {
		return attachmentRepository.existsAttachmentByTargetId(templateId);
	}

	@Transactional
	public void createAttachmentForComment(CreateAttachmentDTO attachmentFile, Long commentId, Long userId) {
		Attachment newComment = Attachment.builder()
						.originName(attachmentFile.getOriginalName())
						.storedName(attachmentFile.getStoredName())
						.url(attachmentFile.getUrl())
						.fileType(attachmentFile.getFileType())
						.size(attachmentFile.getSize())
						.uploadAt(LocalDateTime.now())
						.targetType(FileTargetType.COMMENT)
						.targetId(commentId)
						.uploaderId(userId)
						.isDeleted(false)
						.build();

		attachmentRepository.save(newComment);
	}
}
