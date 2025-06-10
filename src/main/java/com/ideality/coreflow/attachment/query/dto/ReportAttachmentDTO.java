package com.ideality.coreflow.attachment.query.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAttachmentDTO {

	private Long id;
	private String url;
	private String originName;

	private LocalDateTime uploadAt;
	private String uploader;

	private String fileType;
	private String size;
	private String targetType;
	private Long targetId;

}