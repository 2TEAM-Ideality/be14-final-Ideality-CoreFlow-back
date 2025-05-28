package com.ideality.coreflow.template.query.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TemplateListResponseDTO {
	// 템플릿 목록 조회용 DTO
	
	private int id;			// 템플릿 아이디
	private String name;	// 템플릿 이름

	private LocalDateTime createdAt;	// 생성일
	private String createdBy;			// 생성자

	private int duration; 		// 총 소요일
	private int totalTasks; 	// 전체 태스크 개수
	private int usingProjects; 	// 사용 중인 프로젝트 개수

	private List<String> deptList; // 참여 부서 목록

}

//
// {
// 	"id": 1,
// 	"name": "샘플 템플릿",
// 	"description": "제품 출시 전 프로세스",
// 	"nodeList": [...],   // TemplateNodeDTO[]
// 	"edgeList": [...]    // EdgeDTO[]
// 	}

