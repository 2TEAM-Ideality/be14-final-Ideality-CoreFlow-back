package com.ideality.coreflow.attachment.command.application.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

import com.ideality.coreflow.attachment.command.application.dto.CreateAttachmentDTO;
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

	public Boolean findAttachmentByTargetId(Long templateId) {
		return attachmentRepository.existsAttachmentByTargetId(templateId);
	}

	@Transactional
	public void createAttachmentForComment(CreateAttachmentDTO attachmentFile, Long taskId, Long userId) {
		Attachment newComment = Attachment.builder()
						.originName(attachmentFile.getOriginalName())
						.storedName(attachmentFile.getStoredName())
						.url(attachmentFile.getUrl())
						.fileType(attachmentFile.getFileType())
						.size(attachmentFile.getSize())
						.uploadAt(LocalDateTime.now())
						.targetType(FileTargetType.COMMENT)
						.targetId(taskId)
						.uploaderId(userId)
						.isDeleted(false)
						.build();

		attachmentRepository.save(newComment);
	}
}
