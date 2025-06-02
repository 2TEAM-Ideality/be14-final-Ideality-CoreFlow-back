package com.ideality.coreflow.template.query.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateInfoDTO {
	// 템플릿 디테일 정보 조회
	private int id;
	private String name;	// 템플릿 이름
	private String description; // 템플릿 설명
	private LocalDateTime createdAt;	// 생성일
	private String createdBy;			// 생성자
	private int duration;
	private int taskCount;
	private int usingProjects;
	private String deptList;

	public List<String> getDeptList() {
		return deptList != null ? Arrays.asList(deptList.split(",")) : List.of();
	}

}
