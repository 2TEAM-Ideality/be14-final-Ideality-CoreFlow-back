package com.ideality.coreflow.attachment.query.dto;

import java.time.LocalDateTime;
import java.util.List;

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
	private Long uploaderId;
	
	private String taskName; 	// 관련 태스크 이름

	private String fileType;
	private String size;
	private String targetType;   // PROJECT
	private Long targetId;		 // 프로젝트 아이디

	private Long taskId; // 해당하는 태스크 id
	// 담당 부서
	private List<String> deptName;
}