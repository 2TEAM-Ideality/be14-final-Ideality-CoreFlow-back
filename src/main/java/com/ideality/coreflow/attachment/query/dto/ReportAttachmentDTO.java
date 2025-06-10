package com.ideality.coreflow.attachment.query.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ReportAttachmentDTO {

	// 산출물 목록
	private Long id;
	private String url;
	private String originName;

	private LocalDateTime uploadAt;
	private String uploader;

	private String fileType;
	private String size;
	private String targetType;   // PROJECT
	private Long targetId;		 // 프로젝트 아이디

}