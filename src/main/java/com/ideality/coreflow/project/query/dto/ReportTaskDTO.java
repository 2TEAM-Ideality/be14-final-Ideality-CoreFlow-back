package com.ideality.coreflow.project.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReportTaskDTO {

	// 프로젝트 분석 리포트용 - 작업 공정 내역
	private Long id;
	private String name;
	private String description;
	private Double progressRate;

	private String baseLine;		// 베이스라인
	private String realDuration;	// 실제 작업 기간
	private String delay;			// 지연일 ex) +N일, 0일
	private String status;      	// 지연, 정상
	
}
