package com.ideality.coreflow.template.query.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponseTemplateListDTO {
	// 템플릿 목록 조회용 DTO
	
	private int id;			// 템플릿 아이디
	private String name;	// 템플릿 이름
	private LocalDateTime createdAt;	// 생성일
	private String createdBy;			// 생성자
	private int duration; 		// 총 소요일
	private int taskCount; 	// 전체 태스크 개수
	private int usingProjects;

	private List<DeptDTO> deptList;

//	public List<String> getDeptList() {
//		return deptList != null ? Arrays.asList(deptList.split(",")) : List.of();
//	}

}
