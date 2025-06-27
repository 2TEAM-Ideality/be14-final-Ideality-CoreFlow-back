package com.ideality.coreflow.attachment.query.mapper;

import java.util.List;
import java.util.Map;

import com.ideality.coreflow.attachment.query.dto.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttachmentMapper {

	ResponseAttachmentDTO selectUrl(Map<String, Object> paramMap);

	List<ReportAttachmentDTO> selectAttachmentsByProjectId(Long projectId);

	List<ResponseCommentAttachmentDTO> selectAttachmentsByTaskId(Long taskId);

	AttachmentDownloadDTO selectAttachmentInfoForDownload(Long attachmentId);

	AttachmentForCommentDTO selectAttachmentsForComments(Long commentId);

	List<ResponseCommentAttachmentDTO> selectAttachmentsByCommentId(List<Long> commentIds);
}
